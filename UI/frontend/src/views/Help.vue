<template>
  <div class="help-page">
    <div class="main-container">
      <el-container class="help-container">
        <!-- 侧边导航 -->
        <el-aside width="240px" class="help-sidebar">
          <div class="sidebar-title">帮助中心</div>
          <el-menu
            :default-active="activeMenu"
            class="help-menu"
            @select="handleSelect"
          >
            <el-menu-item index="1">
              <el-icon><Goods /></el-icon>
              <span>买家指南</span>
            </el-menu-item>
            <el-menu-item index="2">
              <el-icon><Shop /></el-icon>
              <span>卖家指南</span>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon><Warning /></el-icon>
              <span>交易规则</span>
            </el-menu-item>
            <el-menu-item index="4">
              <el-icon><QuestionFilled /></el-icon>
              <span>常见问题</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 内容区 -->
        <el-main class="help-content">
          <div class="content-box">
            <h2 class="content-title">{{ currentTitle }}</h2>
            
            <div class="article" v-if="activeMenu !== '4'">
              <div v-if="activeMenu === '1'">
                <h3>如何购买商品？</h3>
                <p>1. 浏览商品：在首页或闲置市场浏览您感兴趣的商品。<br>2. 联系卖家：点击商品详情页的"联系卖家"按钮，进行沟通。<br>3. 下单支付：确认无误后，点击"立即购买"，目前支持线上支付和当面交易。</p>
                <h3>交易安全提示</h3>
                <p>请勿在平台外（如微信、QQ）直接转账，谨防诈骗。建议优先选择当面交易，一手交钱一手交货。</p>
              </div>
              <div v-if="activeMenu === '2'">
                <h3>如何发布商品？</h3>
                <p>点击右上角的"我要发布"按钮，填写商品标题、描述、价格，上传清晰的图片，选择分类后即可发布。</p>
                <h3>如何提高曝光率？</h3>
                <p>1. 拍摄清晰美观的照片。<br>2. 填写详细准确的描述。<br>3. 价格设置合理。<br>4. 保持良好的信用记录。</p>
              </div>
              <div v-if="activeMenu === '3'">
                <h3>违规行为处理</h3>
                <p>发布虚假信息、恶意刷单、辱骂他人等行为将被扣除信用分，严重者将被封号。</p>
                <h3>退换货规则</h3>
                <p>二手商品特殊性，建议交易前充分沟通。若商品与描述严重不符，买家有权申请退货。</p>
              </div>
            </div>

            <el-collapse v-else v-model="activeName" accordion>
              <el-collapse-item title="忘记密码怎么办？" name="1">
                <div>请在登录页面点击"忘记密码"，通过注册邮箱找回。</div>
              </el-collapse-item>
              <el-collapse-item title="实名认证安全吗？" name="2">
                <div>平台严格保护用户隐私，实名认证信息仅用于核实校园身份，不会对外公开。</div>
              </el-collapse-item>
              <el-collapse-item title="如何举报违规用户？" name="3">
                <div>在商品详情页或用户主页，点击右上角的"..."按钮，选择"举报"。</div>
              </el-collapse-item>
              <el-collapse-item title="交易纠纷如何处理？" name="4">
                <div>请保留聊天记录和交易凭证，联系平台客服介入处理。</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Help.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, computed } from 'vue'
import { Goods, Shop, Warning, QuestionFilled } from '@element-plus/icons-vue'

const activeMenu = ref('1')
const activeName = ref('1')

const currentTitle = computed(() => {
  const map = {
    '1': '买家指南',
    '2': '卖家指南',
    '3': '交易规则',
    '4': '常见问题'
  }
  return map[activeMenu.value]
})

const handleSelect = (index) => {
  activeMenu.value = index
}
</script>

<style scoped>
.help-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.help-container {
  gap: 20px;
}

.help-sidebar {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.sidebar-title {
  padding: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
}

.help-menu {
  border-right: none;
}

.help-content {
  padding: 0;
  background: #fff;
  border-radius: 8px;
  min-height: 600px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.content-box {
  padding: 40px;
}

.content-title {
  font-size: 24px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.article h3 {
  font-size: 18px;
  margin: 24px 0 12px;
  color: #333;
  border-left: 4px solid #3e6bda;
  padding-left: 10px;
}

.article p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}
</style>










