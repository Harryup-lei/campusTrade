<template>
  <div class="about-page">
    <div class="main-container">
      <div class="about-box">
        <div class="logo-section">
          <div class="logo-circle">🎓</div>
          <h1>校园二手交易平台</h1>
          <p class="slogan">连接每一份闲置与热爱</p>
        </div>

        <div class="content-section">
          <h2>关于我们</h2>
          <p>校园二手交易平台致力于为在校师生提供一个安全、便捷、高效的闲置物品流转平台。我们深知毕业季的烦恼，也理解淘宝的乐趣。在这里，每一件闲置物品都能找到它的新主人，延续它的价值。</p>
          <p>平台严格实行实名认证制度，仅限校内师生使用，最大程度保障交易安全。我们提倡绿色环保、循环利用的生活方式。</p>
        </div>

        <div class="contact-section">
          <h2>联系我们</h2>
          <div class="contact-grid">
            <div class="contact-item">
              <el-icon class="icon"><Phone /></el-icon>
              <div class="info">
                <h4>客服电话</h4>
                <p>400-123-4567</p>
              </div>
            </div>
            <div class="contact-item">
              <el-icon class="icon"><Message /></el-icon>
              <div class="info">
                <h4>电子邮箱</h4>
                <p>support@campus-trade.com</p>
              </div>
            </div>
            <div class="contact-item">
              <el-icon class="icon"><Location /></el-icon>
              <div class="info">
                <h4>办公地址</h4>
                <p>XX大学 大学生创业孵化中心 302室</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/About.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { Phone, Message, Location } from '@element-plus/icons-vue'
</script>

<style scoped>
.about-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 40px 0;
}

.main-container {
  width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

.about-box {
  background: #fff;
  border-radius: 12px;
  padding: 60px 80px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo-circle {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #3e6bda 0%, #578bf9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: #fff;
  margin: 0 auto 24px;
  box-shadow: 0 8px 24px rgba(62, 107, 218, 0.3);
}

.logo-section h1 {
  font-size: 32px;
  color: #333;
  margin: 0 0 12px 0;
}

.slogan {
  font-size: 18px;
  color: #666;
  margin: 0;
  letter-spacing: 2px;
}

.content-section {
  margin-bottom: 60px;
  text-align: center;
}

.content-section h2, .contact-section h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 24px;
  position: relative;
  display: inline-block;
}

.content-section h2::after, .contact-section h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 4px;
  background: #3e6bda;
  border-radius: 2px;
}

.content-section p {
  font-size: 16px;
  color: #555;
  line-height: 1.8;
  max-width: 800px;
  margin: 0 auto 16px;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-top: 40px;
}

.contact-item {
  background: #f9fafc;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s;
}

.contact-item:hover {
  background: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transform: translateY(-4px);
}

.icon {
  font-size: 32px;
  color: #3e6bda;
  margin-bottom: 16px;
}

.info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.info p {
  font-size: 14px;
  color: #666;
  margin: 0;
}
</style>










