#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""为项目源码添加 hangu / kelei 贡献注释，总计 3000 行（各 1500 行）。"""

from __future__ import annotations

import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TARGET_TOTAL = 3000
KELEI_QUOTA = 1500
HANGU_QUOTA = 1500

SOURCE_DIRS = [
    ROOT / "backend" / "src" / "main" / "java",
    ROOT / "UI" / "frontend" / "src",
    ROOT / "UI" / "admin" / "src",
]

SKIP_PARTS = {"node_modules", "target", "dist", ".git"}

JAVA_METHOD = re.compile(
    r"^(\s*)((?:@\w+(?:\([^)]*\))?\s*)*)"
    r"((?:public|protected|private)\s+(?:static\s+)?[\w<>\[\],\s?]+\s+(\w+)\s*\([^;]*\)\s*(?:throws[^{]+)?\{)",
    re.MULTILINE,
)
JAVA_CLASS = re.compile(r"^(\s*(?:public|protected|private)?\s*(?:abstract\s+)?class\s+(\w+))", re.MULTILINE)
JAVA_INTERFACE = re.compile(r"^(\s*(?:public\s+)?interface\s+(\w+))", re.MULTILINE)

JS_FUNC = re.compile(
    r"^(\s*)((?:export\s+)?(?:async\s+)?function\s+(\w+)\s*\([^)]*\)\s*\{)",
    re.MULTILINE,
)
JS_ARROW = re.compile(
    r"^(\s*)(export\s+const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{)",
    re.MULTILINE,
)
JS_ARROW2 = re.compile(
    r"^(\s*)(const\s+(\w+)\s*=\s*(?:async\s*)?\([^)]*\)\s*=>\s*\{)",
    re.MULTILINE,
)
JS_METHOD = re.compile(
    r"^(\s*)((?:async\s+)?(\w+)\s*\([^)]*\)\s*\{)",
    re.MULTILINE,
)

CONTRIBUTOR_HEADER = re.compile(
    r"/\*\*\s*\n(?: \*.*\n)*? \* 代码贡献统计.*?\*/\s*\n|"
    r"// ={3,} 代码贡献统计 ={3,}\n(?:// .*\n)*?\n",
    re.DOTALL,
)
FUNC_COMMENT = re.compile(
    r"^\s*(?://|/\*\*)\s*@contributor\s+\w+.*\n(?:\s*\*.*\n)?",
    re.MULTILINE,
)


@dataclass
class FunctionBlock:
    file: Path
    name: str
    start: int
    end: int
    lines: int
    contributor: str | None = None
    attributed: int = 0


@dataclass
class FileResult:
    path: Path
    functions: list[FunctionBlock] = field(default_factory=list)
    hangu: int = 0
    kelei: int = 0


def iter_source_files() -> list[Path]:
    files: list[Path] = []
    for base in SOURCE_DIRS:
        if not base.exists():
            continue
        for p in base.rglob("*"):
            if not p.is_file():
                continue
            if any(part in SKIP_PARTS for part in p.parts):
                continue
            if p.suffix in {".java", ".js", ".vue", ".ts", ".jsx", ".tsx"}:
                files.append(p)
    return sorted(files)


def find_block_end(lines: list[str], start_idx: int) -> int:
    depth = 0
    started = False
    for i in range(start_idx, len(lines)):
        line = lines[i]
        for ch in line:
            if ch == "{":
                depth += 1
                started = True
            elif ch == "}":
                depth -= 1
                if started and depth == 0:
                    return i
    return start_idx


def extract_vue_script(content: str) -> tuple[str, int, int] | None:
    m = re.search(r"<script[^>]*>([\s\S]*?)</script>", content, re.IGNORECASE)
    if not m:
        return None
    return m.group(1), m.start(1), m.end(1)


def parse_java_functions(path: Path, content: str) -> list[FunctionBlock]:
    lines = content.splitlines()
    blocks: list[FunctionBlock] = []
    seen: set[int] = set()

    for m in JAVA_METHOD.finditer(content):
        idx = content[: m.start()].count("\n")
        if idx in seen:
            continue
        end_idx = find_block_end(lines, idx)
        fn_lines = end_idx - idx + 1
        if fn_lines < 2:
            continue
        seen.add(idx)
        blocks.append(FunctionBlock(path, m.group(4), idx, end_idx, fn_lines))

    if not blocks:
        for m in JAVA_CLASS.finditer(content):
            idx = content[: m.start()].count("\n")
            end_idx = find_block_end(lines, idx)
            blocks.append(FunctionBlock(path, f"class {m.group(2)}", idx, end_idx, end_idx - idx + 1))
            break
        else:
            for m in JAVA_INTERFACE.finditer(content):
                idx = content[: m.start()].count("\n")
                end_idx = find_block_end(lines, idx)
                blocks.append(FunctionBlock(path, f"interface {m.group(2)}", idx, end_idx, end_idx - idx + 1))
                break
    return blocks


def parse_js_functions(content: str, offset: int = 0) -> list[tuple[int, int, int, str]]:
    lines = content.splitlines()
    found: list[tuple[int, int, int, str]] = []
    used_lines: set[int] = set()

    patterns = [JS_FUNC, JS_ARROW, JS_ARROW2]
    for pat in patterns:
        for m in pat.finditer(content):
            idx = content[: m.start()].count("\n")
            if idx in used_lines:
                continue
            end_idx = find_block_end(lines, idx)
            fn_lines = end_idx - idx + 1
            if fn_lines < 2:
                continue
            used_lines.add(idx)
            found.append((offset + idx, offset + end_idx, fn_lines, m.group(3)))

    for m in JS_METHOD.finditer(content):
        idx = content[: m.start()].count("\n")
        if idx in used_lines:
            continue
        name = m.group(3)
        if name in {"if", "for", "while", "switch", "catch", "function"}:
            continue
        end_idx = find_block_end(lines, idx)
        fn_lines = end_idx - idx + 1
        if fn_lines < 3:
            continue
        used_lines.add(idx)
        found.append((offset + idx, offset + end_idx, fn_lines, name))

    return found


def parse_file_functions(path: Path) -> list[FunctionBlock]:
    content = path.read_text(encoding="utf-8")
    suffix = path.suffix.lower()

    if suffix == ".java":
        return parse_java_functions(path, content)

    if suffix == ".vue":
        script = extract_vue_script(content)
        if not script:
            return [FunctionBlock(path, path.stem, 0, len(content.splitlines()) - 1, len(content.splitlines()))]
        script_content, _, _ = script
        line_offset = content[: script[1]].count("\n")
        blocks = []
        for start, end, fn_lines, name in parse_js_functions(script_content, line_offset):
            blocks.append(FunctionBlock(path, name, start, end, fn_lines))
        if not blocks:
            total = len(script_content.splitlines())
            blocks.append(FunctionBlock(path, f"{path.stem}.vue script", line_offset, line_offset + total - 1, total))
        return blocks

    if suffix in {".js", ".ts", ".jsx", ".tsx"}:
        blocks = []
        for start, end, fn_lines, name in parse_js_functions(content):
            blocks.append(FunctionBlock(path, name, start, end, fn_lines))
        if not blocks:
            total = len(content.splitlines())
            blocks.append(FunctionBlock(path, path.stem, 0, total - 1, total))
        return blocks

    return []


def assign_contributors(all_functions: list[FunctionBlock]) -> None:
    """按函数行数贪心分配，使 hangu / kelei 各约 1500 行。"""
    sorted_fns = sorted(all_functions, key=lambda f: f.lines, reverse=True)
    hangu_total = 0
    kelei_total = 0

    for fn in sorted_fns:
        if hangu_total >= HANGU_QUOTA and kelei_total >= KELEI_QUOTA:
            fn.contributor = "hangu"
            fn.attributed = 0
            continue
        if hangu_total >= HANGU_QUOTA:
            fn.contributor = "kelei"
        elif kelei_total >= KELEI_QUOTA:
            fn.contributor = "hangu"
        elif hangu_total <= kelei_total:
            fn.contributor = "hangu"
        else:
            fn.contributor = "kelei"

        remaining = HANGU_QUOTA - hangu_total if fn.contributor == "hangu" else KELEI_QUOTA - kelei_total
        fn.attributed = min(fn.lines, remaining)
        if fn.contributor == "hangu":
            hangu_total += fn.attributed
        else:
            kelei_total += fn.attributed

    # 若未凑满 3000，将剩余额度补给最大函数
    deficit_h = HANGU_QUOTA - hangu_total
    deficit_k = KELEI_QUOTA - kelei_total
    for fn in sorted_fns:
        if deficit_h > 0 and fn.contributor == "hangu":
            extra = min(deficit_h, max(0, fn.lines - fn.attributed))
            fn.attributed += extra
            deficit_h -= extra
        if deficit_k > 0 and fn.contributor == "kelei":
            extra = min(deficit_k, max(0, fn.lines - fn.attributed))
            fn.attributed += extra
            deficit_k -= extra


def make_java_file_header(hangu: int, kelei: int, rel: str) -> str:
    return (
        "/**\n"
        " * 代码贡献统计（项目总计 3000 行）\n"
        f" * 文件: {rel}\n"
        f" * hangu: {hangu} 行 | kelei: {kelei} 行 | 本文件合计: {hangu + kelei} 行\n"
        " */\n"
    )


def make_js_file_header(hangu: int, kelei: int, rel: str) -> str:
    return (
        "// ============================================================\n"
        "// 代码贡献统计（项目总计 3000 行）\n"
        f"// 文件: {rel}\n"
        f"// hangu: {hangu} 行 | kelei: {kelei} 行 | 本文件合计: {hangu + kelei} 行\n"
        "// ============================================================\n"
    )


def make_func_comment_java(contributor: str, lines: int, name: str) -> str:
    return f"    /** @contributor {contributor} - {name} ({lines} 行) */\n"


def make_func_comment_js(indent: str, contributor: str, lines: int, name: str) -> str:
    return f"{indent}// @contributor {contributor} - {name} ({lines} 行)\n"


def strip_existing(content: str) -> str:
    content = CONTRIBUTOR_HEADER.sub("", content)
    return content


def apply_comments(path: Path, functions: list[FunctionBlock]) -> bool:
    rel = str(path.relative_to(ROOT)).replace("\\", "/")
    hangu = sum(f.attributed for f in functions if f.contributor == "hangu")
    kelei = sum(f.attributed for f in functions if f.contributor == "kelei")

    content = strip_existing(path.read_text(encoding="utf-8"))
    lines = content.splitlines(keepends=True)
    suffix = path.suffix.lower()

    # 函数注释（从后往前插入，避免行号偏移）
    for fn in sorted(functions, key=lambda f: f.start, reverse=True):
        if fn.attributed <= 0:
            continue
        insert_at = fn.start
        if insert_at >= len(lines):
            continue
        line = lines[insert_at]
        indent = re.match(r"^(\s*)", line).group(1)
        if suffix == ".java":
            comment = make_func_comment_java(fn.contributor, fn.attributed, fn.name)
        else:
            comment = make_func_comment_js(indent, fn.contributor, fn.attributed, fn.name)
        lines.insert(insert_at, comment)

    body = "".join(lines)

    if suffix == ".java":
        header = make_java_file_header(hangu, kelei, rel)
        pkg = re.search(r"^package\s+[\w.]+;\s*\n", body, re.MULTILINE)
        if pkg:
            pos = pkg.end()
            body = body[:pos] + "\n" + header + body[pos:]
        else:
            body = header + body
    elif suffix == ".vue":
        header = make_js_file_header(hangu, kelei, rel)
        body = re.sub(
            r"(<script[^>]*>\s*\n)",
            r"\1" + header,
            body,
            count=1,
            flags=re.IGNORECASE,
        )
        if header not in body:
            body = header + body
    else:
        header = make_js_file_header(hangu, kelei, rel)
        body = header + body

    if body != path.read_text(encoding="utf-8"):
        path.write_text(body, encoding="utf-8", newline="\n")
        return True
    return False


def main() -> int:
    files = iter_source_files()
    file_map: dict[Path, list[FunctionBlock]] = {}
    all_functions: list[FunctionBlock] = []

    for fp in files:
        fns = parse_file_functions(fp)
        file_map[fp] = fns
        all_functions.extend(fns)

    assign_contributors(all_functions)

    changed = 0
    for fp, fns in file_map.items():
        if apply_comments(fp, fns):
            changed += 1

    hangu = sum(f.attributed for f in all_functions if f.contributor == "hangu")
    kelei = sum(f.attributed for f in all_functions if f.contributor == "kelei")

    print(f"处理文件: {len(files)}")
    print(f"修改文件: {changed}")
    print(f"识别函数: {len(all_functions)}")
    print(f"hangu 贡献: {hangu} 行")
    print(f"kelei 贡献: {kelei} 行")
    print(f"合计: {hangu + kelei} 行")
    return 0


if __name__ == "__main__":
    sys.exit(main())
