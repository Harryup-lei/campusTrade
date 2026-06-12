# Contributor comment injector (ASCII-only script)
$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent $PSScriptRoot
$Labels = Get-Content (Join-Path $PSScriptRoot 'contributor_labels.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$HanguQuota = 1500
$KeleiQuota = 1500
$NL = [Environment]::NewLine

$SourceDirs = @(
    (Join-Path $Root 'backend\src\main\java')
    (Join-Path $Root 'UI\frontend\src')
    (Join-Path $Root 'UI\admin\src')
)

function Get-SourceFiles {
    $files = @()
    foreach ($dir in $SourceDirs) {
        if (-not (Test-Path $dir)) { continue }
        $files += Get-ChildItem -Path $dir -Recurse -File -Include *.java,*.js,*.vue,*.ts,*.jsx,*.tsx |
            Where-Object { $_.FullName -notmatch 'node_modules|\\target\\|\\dist\\' }
    }
    return $files | Sort-Object FullName
}

function Find-BlockEnd {
    param([string[]]$Lines, [int]$StartIdx)
    $depth = 0; $started = $false
    for ($i = $StartIdx; $i -lt $Lines.Count; $i++) {
        foreach ($ch in $Lines[$i].ToCharArray()) {
            if ($ch -eq '{') { $depth++; $started = $true }
            elseif ($ch -eq '}') { $depth--; if ($started -and $depth -eq 0) { return $i } }
        }
    }
    return $StartIdx
}

function New-FunctionBlock {
    param($File, $Name, $Start, $End, $Lines)
    return [PSCustomObject]@{ File = $File; Name = $Name; Start = $Start; End = $End; Lines = $Lines; Contributor = $null; Attributed = 0 }
}

function Parse-JavaFunctions {
    param([string]$Content, [System.IO.FileInfo]$File)
    $lines = $Content -split "`r?`n"
    $blocks = @(); $seen = @{}
    $methodPattern = '(?m)^(\s*)((?:@\w+(?:\([^)]*\))?\s*)*)((?:public|protected|private)\s+(?:static\s+)?[\w<>\[\],\s?]+\s+(\w+)\s*\([^;]*\)\s*(?:throws[^{]+)?\{)'
    foreach ($m in [regex]::Matches($Content, $methodPattern)) {
        $idx = ($Content.Substring(0, $m.Index) -split "`r?`n").Count - 1
        if ($seen.ContainsKey($idx)) { continue }
        $endIdx = Find-BlockEnd -Lines $lines -StartIdx $idx
        $fnLines = $endIdx - $idx + 1
        if ($fnLines -lt 2) { continue }
        $seen[$idx] = $true
        $blocks += New-FunctionBlock $File $m.Groups[4].Value $idx $endIdx $fnLines
    }
    if ($blocks.Count -eq 0) {
        $cm = [regex]::Match($Content, '(?m)^(\s*(?:public|protected|private)?\s*(?:abstract\s+)?class\s+(\w+))')
        if ($cm.Success) {
            $idx = ($Content.Substring(0, $cm.Index) -split "`r?`n").Count - 1
            $endIdx = Find-BlockEnd -Lines $lines -StartIdx $idx
            $blocks += New-FunctionBlock $File ('class ' + $cm.Groups[2].Value) $idx $endIdx ($endIdx - $idx + 1)
        }
    }
    return $blocks
}

function Parse-JsFunctions {
    param([string]$Content, [int]$LineOffset = 0)
    $lines = $Content -split "`r?`n"
    $found = @(); $used = @{}
    $blockPatterns = @(
        '(?m)^(\s*)((?:export\s+)?(?:async\s+)?function\s+(\w+)\s*\([^)]*\)\s*\{)',
        '(?m)^(\s*)(export\s+const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{)',
        '(?m)^(\s*)(const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{)'
    )
    foreach ($pat in $blockPatterns) {
        foreach ($m in [regex]::Matches($Content, $pat)) {
            $idx = ($Content.Substring(0, $m.Index) -split "`r?`n").Count - 1
            if ($used.ContainsKey($idx)) { continue }
            $endIdx = Find-BlockEnd -Lines $lines -StartIdx $idx
            $fnLines = $endIdx - $idx + 1
            if ($fnLines -lt 1) { continue }
            $used[$idx] = $true
            $found += [PSCustomObject]@{ Start = ($LineOffset + $idx); End = ($LineOffset + $endIdx); Lines = $fnLines; Name = $m.Groups[3].Value }
        }
    }
    $singlePatterns = @(
        '(?m)^(\s*)(export\s+const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*.+)$',
        '(?m)^(\s*)(const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*.+)$'
    )
    foreach ($pat in $singlePatterns) {
        foreach ($m in [regex]::Matches($Content, $pat)) {
            $idx = ($Content.Substring(0, $m.Index) -split "`r?`n").Count - 1
            if ($used.ContainsKey($idx)) { continue }
            $used[$idx] = $true
            $found += [PSCustomObject]@{ Start = ($LineOffset + $idx); End = ($LineOffset + $idx); Lines = 1; Name = $m.Groups[3].Value }
        }
    }
    $methodPattern = '(?m)^(\s*)((?:async\s+)?(\w+)\s*\([^)]*\)\s*\{)'
    foreach ($m in [regex]::Matches($Content, $methodPattern)) {
        $idx = ($Content.Substring(0, $m.Index) -split "`r?`n").Count - 1
        if ($used.ContainsKey($idx)) { continue }
        $name = $m.Groups[3].Value
        if ($name -in @('if','for','while','switch','catch','function')) { continue }
        $endIdx = Find-BlockEnd -Lines $lines -StartIdx $idx
        $fnLines = $endIdx - $idx + 1
        if ($fnLines -lt 2) { continue }
        $used[$idx] = $true
        $found += [PSCustomObject]@{ Start = ($LineOffset + $idx); End = ($LineOffset + $endIdx); Lines = $fnLines; Name = $name }
    }
    return $found
}

function Parse-FileFunctions {
    param([System.IO.FileInfo]$File)
    $content = [System.IO.File]::ReadAllText($File.FullName, [System.Text.Encoding]::UTF8)
    $ext = $File.Extension.ToLower()
    if ($ext -eq '.java') { return Parse-JavaFunctions -Content $content -File $File }
    if ($ext -eq '.vue') {
        $sm = [regex]::Match($content, '(?is)<script[^>]*>([\s\S]*?)</script>')
        if (-not $sm.Success) {
            $total = ($content -split "`r?`n").Count
            return @(New-FunctionBlock $File ($File.BaseName + '.vue') 0 ($total - 1) $total)
        }
        $script = $sm.Groups[1].Value
        $lineOffset = ($content.Substring(0, $sm.Groups[1].Index) -split "`r?`n").Count - 1
        $blocks = @()
        foreach ($f in (Parse-JsFunctions -Content $script -LineOffset $lineOffset)) {
            $blocks += New-FunctionBlock $File $f.Name $f.Start $f.End $f.Lines
        }
        if ($blocks.Count -eq 0) {
            $total = ($script -split "`r?`n").Count
            $blocks += New-FunctionBlock $File ($File.BaseName + ' script') $lineOffset ($lineOffset + $total - 1) $total
        }
        return $blocks
    }
    if ($ext -in @('.js','.ts','.jsx','.tsx')) {
        $blocks = @()
        foreach ($f in (Parse-JsFunctions -Content $content)) {
            $blocks += New-FunctionBlock $File $f.Name $f.Start $f.End $f.Lines
        }
        if ($blocks.Count -eq 0) {
            $total = ($content -split "`r?`n").Count
            $blocks += New-FunctionBlock $File $File.BaseName 0 ($total - 1) $total
        }
        return $blocks
    }
    return @()
}

function Assign-Contributors {
    param([array]$AllFunctions)
    $sorted = $AllFunctions | Sort-Object Lines -Descending
    $hanguTotal = 0; $keleiTotal = 0
    foreach ($fn in $sorted) {
        if ($hanguTotal -ge $HanguQuota -and $keleiTotal -ge $KeleiQuota) { $fn.Contributor = 'hangu'; $fn.Attributed = 0; continue }
        if ($hanguTotal -ge $HanguQuota) { $fn.Contributor = 'kelei' }
        elseif ($keleiTotal -ge $KeleiQuota) { $fn.Contributor = 'hangu' }
        elseif ($hanguTotal -le $keleiTotal) { $fn.Contributor = 'hangu' }
        else { $fn.Contributor = 'kelei' }
        $remaining = if ($fn.Contributor -eq 'hangu') { $HanguQuota - $hanguTotal } else { $KeleiQuota - $keleiTotal }
        $fn.Attributed = [Math]::Min($fn.Lines, $remaining)
        if ($fn.Contributor -eq 'hangu') { $hanguTotal += $fn.Attributed } else { $keleiTotal += $fn.Attributed }
    }
    foreach ($fn in $sorted) {
        if ($hanguTotal -lt $HanguQuota -and $fn.Contributor -eq 'hangu') {
            $extra = [Math]::Min(($HanguQuota - $hanguTotal), [Math]::Max(0, ($fn.Lines - $fn.Attributed)))
            $fn.Attributed += $extra; $hanguTotal += $extra
        }
        if ($keleiTotal -lt $KeleiQuota -and $fn.Contributor -eq 'kelei') {
            $extra = [Math]::Min(($KeleiQuota - $keleiTotal), [Math]::Max(0, ($fn.Lines - $fn.Attributed)))
            $fn.Attributed += $extra; $keleiTotal += $extra
        }
    }
}

function Strip-ExistingComments {
    param([string]$Content)
    $prev = ''
    while ($prev -ne $Content) {
        $prev = $Content
        $Content = [regex]::Replace($Content, '(?s)/\*\*(?:(?!\*/).)*?(?:contributor|3000)(?:(?!\*/).)*?\*/\s*\r?\n', '')
        $Content = [regex]::Replace($Content, '(?s)/\*\*\s*@contributor.*?\*/\s*\r?\n', '')
        $Content = [regex]::Replace($Content, '(?m)^\s*// @contributor.*\r?\n', '')
        $Content = [regex]::Replace($Content, '(?m)^(?:// .*(?:contributor|====|hangu|kelei|3000).*\r?\n)+', '')
    }
    # fix indentation drift from prior inserts
    $Content = [regex]::Replace($Content, '(?m)^        @(Get|Post|Put|Delete|Patch|Request)Mapping', '    @$1Mapping')
    return $Content
}

function Remove-GarbageCommentLines {
    param([string]$Content)
    $lines = ($Content -split "`r?`n")
    $out = New-Object System.Collections.Generic.List[string]
    foreach ($line in $lines) {
        if ($line.StartsWith('// ') -and $line.Contains('文件:')) { continue }
        if ($line.StartsWith('// ') -and $line -match ':\s*UI/' -and $line -notmatch '3000') { continue }
        if ($line -match 'hangu: 0 .* kelei: 0') { continue }
        [void]$out.Add($line)
    }
    return (($out -join $NL) + $NL)
}

function Get-InsertLine {
    param([System.Collections.Generic.List[string]]$LineList, [int]$Start)
    $insertAt = $Start
    while ($insertAt -gt 0) {
        $prev = $LineList[$insertAt - 1].Trim()
        if ($prev -match '^@(Get|Post|Put|Delete|Patch|Request)Mapping\b') {
            return ($insertAt - 1)
        }
        if ($prev -match '^@contributor' -or $prev -match '@contributor') {
            $insertAt--
            continue
        }
        break
    }
    return $insertAt
}

function Build-JavaHeader {
    param([string]$Rel, [int]$Hangu, [int]$Kelei)
    $u = $Labels.linesUnit
    $line3 = ' * ' + $Labels.hanguLabel + ': ' + $Hangu + ' ' + $u + ' | ' + $Labels.keleiLabel + ': ' + $Kelei + ' ' + $u + ' | ' + $Labels.fileTotalLabel + ': ' + ($Hangu + $Kelei) + ' ' + $u
    $sb = New-Object System.Text.StringBuilder
    [void]$sb.AppendLine('/**')
    [void]$sb.AppendLine(' * ' + $Labels.title)
    [void]$sb.AppendLine(' * ' + $Labels.fileLabel + ': ' + $Rel)
    [void]$sb.AppendLine($line3)
    [void]$sb.AppendLine(' */')
    return $sb.ToString()
}

function Build-JsHeader {
    param([string]$Rel, [int]$Hangu, [int]$Kelei)
    $u = $Labels.linesUnit
    $line3 = [string][char]0x2F + [string][char]0x2F + ' ' + $Labels.hanguLabel + ': ' + $Hangu + ' ' + $u + ' | ' + $Labels.keleiLabel + ': ' + $Kelei + ' ' + $u + ' | ' + $Labels.fileTotalLabel + ': ' + ($Hangu + $Kelei) + ' ' + $u
    $sb = New-Object System.Text.StringBuilder
    [void]$sb.AppendLine([string][char]0x2F + [string][char]0x2F + ' ============================================================')
    [void]$sb.AppendLine([string][char]0x2F + [string][char]0x2F + ' ' + $Labels.title)
    [void]$sb.AppendLine([string][char]0x2F + [string][char]0x2F + ' ' + $Labels.fileLabel + ': ' + $Rel)
    [void]$sb.AppendLine($line3)
    [void]$sb.AppendLine([string][char]0x2F + [string][char]0x2F + ' ============================================================')
    return $sb.ToString()
}

function Apply-Comments {
    param([System.IO.FileInfo]$File, [array]$Functions)
    $rel = $File.FullName.Substring($Root.Length).TrimStart('\','/') -replace '\\','/'
    $hangu = [int](($Functions | Where-Object { $_.Contributor -eq 'hangu' } | Measure-Object -Property Attributed -Sum).Sum)
    $kelei = [int](($Functions | Where-Object { $_.Contributor -eq 'kelei' } | Measure-Object -Property Attributed -Sum).Sum)
    if (-not $hangu) { $hangu = 0 }; if (-not $kelei) { $kelei = 0 }

    $original = [System.IO.File]::ReadAllText($File.FullName, [System.Text.Encoding]::UTF8)
    $content = Remove-GarbageCommentLines (Strip-ExistingComments $original)
    $lineList = [System.Collections.Generic.List[string]]::new()
    foreach ($l in ($content -split "`r?`n", [System.StringSplitOptions]::None)) { [void]$lineList.Add($l) }

    $ext = $File.Extension.ToLower()
    $u = $Labels.linesUnit
    $tag = $Labels.contributorTag
    foreach ($fn in ($Functions | Sort-Object Start -Descending)) {
        if ($fn.Attributed -le 0 -or $fn.Start -ge $lineList.Count) { continue }
        if ($ext -eq '.java') {
            $sigLine = $lineList[$fn.Start]
            if ($sigLine -notmatch '^\s*(public|protected|private).*\)\s*\{') { continue }
        }
        $insertAt = Get-InsertLine -LineList $lineList -Start $fn.Start
        $indent = ([regex]::Match($lineList[$insertAt], '^(\s*)')).Groups[1].Value
        if ($ext -eq '.java') {
            $comment = $indent + '/** ' + $tag + ' ' + $fn.Contributor + ' - ' + $fn.Name + ' (' + $fn.Attributed + ' ' + $u + ') */'
        } else {
            $comment = $indent + [string][char]0x2F + [string][char]0x2F + ' ' + $tag + ' ' + $fn.Contributor + ' - ' + $fn.Name + ' (' + $fn.Attributed + ' ' + $u + ')'
        }
        $lineList.Insert($insertAt, $comment)
    }

    $body = ($lineList -join $NL) + $NL
    if ($ext -eq '.java') {
        $header = Build-JavaHeader $rel $hangu $kelei
        $classMatch = [regex]::Match($body, '(?m)^(@\w+|public\s+(?:abstract\s+)?class\s+)')
        if ($classMatch.Success) {
            $body = $body.Insert($classMatch.Index, $header)
        } else {
            $pkg = [regex]::Match($body, '(?m)^package\s+[\w.]+;\s*\r?\n')
            if ($pkg.Success) {
                $pos = $pkg.Index + $pkg.Length
                $body = $body.Insert($pos, $NL + $header)
            } else { $body = $header + $body }
        }
    }
    elseif ($ext -eq '.vue') {
        $header = Build-JsHeader $rel $hangu $kelei
        $body = [regex]::Replace($body, '(?i)(<script[^>]*>\s*\r?\n)', ('${1}' + $header), 1)
    }
    else {
        $header = Build-JsHeader $rel $hangu $kelei
        $body = $header + $body
    }

    if ($body -ne $original) {
        $utf8 = New-Object System.Text.UTF8Encoding $false
        [System.IO.File]::WriteAllText($File.FullName, $body, $utf8)
        return $true
    }
    return $false
}

$files = Get-SourceFiles
$fileMap = @{}; $allFunctions = @()
foreach ($f in $files) {
    $fns = Parse-FileFunctions -File $f
    $fileMap[$f.FullName] = $fns
    $allFunctions += $fns
}
Assign-Contributors -AllFunctions $allFunctions
$changed = 0
foreach ($kv in $fileMap.GetEnumerator()) {
    if (Apply-Comments -File (Get-Item $kv.Key) -Functions $kv.Value) { $changed++ }
}
$hanguSum = [int](($allFunctions | Where-Object { $_.Contributor -eq 'hangu' } | Measure-Object -Property Attributed -Sum).Sum)
$keleiSum = [int](($allFunctions | Where-Object { $_.Contributor -eq 'kelei' } | Measure-Object -Property Attributed -Sum).Sum)
Write-Host ('Files: ' + $files.Count + ', Changed: ' + $changed + ', Functions: ' + $allFunctions.Count)
Write-Host ('hangu: ' + $hanguSum + ', kelei: ' + $keleiSum + ', total: ' + ($hanguSum + $keleiSum))
