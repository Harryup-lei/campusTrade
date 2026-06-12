<template>
  <div class="profile-page">
    <div class="main-container">
      <div class="profile-layout">
        <!-- 左侧：个人简报 -->
        <div class="profile-sidebar">
          <el-card class="user-card">
            <div class="avatar-box">
              <el-avatar :size="100" :src="userStore.userInfo?.avatar || defaultAvatar" />
              <div class="role-tag">{{ getRoleText(userStore.userInfo?.role) }}</div>
            </div>
            <h3 class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
            <p class="username">@{{ userStore.userInfo?.username }}</p>
            
            <el-divider />
            
            <div class="stats">
              <div class="stat-item">
                <div class="num">{{ userStats.publishCount || 0 }}</div>
                <div class="label">发布</div>
              </div>
              <div class="stat-item">
                <div class="num">{{ userStats.soldCount || 0 }}</div>
                <div class="label">卖出</div>
              </div>
              <div class="stat-item">
                <div class="num">{{ userStats.boughtCount || 0 }}</div>
                <div class="label">买入</div>
              </div>
            </div>
          </el-card>

          <el-menu
            :default-active="activeTab"
            class="profile-menu"
            @select="handleSelect"
          >
            <el-menu-item index="info">
              <el-icon><User /></el-icon>
              <span>基本资料</span>
            </el-menu-item>
            <el-menu-item index="password">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
            <el-menu-item index="verify">
              <el-icon><CreditCard /></el-icon>
              <span>实名认证</span>
            </el-menu-item>
            <el-menu-item index="my-products">
              <el-icon><Goods /></el-icon>
              <span>我的发布</span>
            </el-menu-item>
            <el-menu-item index="my-wants">
              <el-icon><Plus /></el-icon>
              <span>我的求购</span>
            </el-menu-item>
            <el-menu-item index="my-offers">
              <el-icon><Money /></el-icon>
              <span>我的出价</span>
            </el-menu-item>
            <el-menu-item index="my-free">
              <el-icon><Present /></el-icon>
              <span>我的赠送</span>
            </el-menu-item>
            <el-menu-item index="my-orders">
              <el-icon><List /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="notifications">
              <el-icon><Bell /></el-icon>
              <span>我的通知</span>
              <el-badge v-if="unreadCount > 0" :value="unreadCount" class="notification-badge-menu" />
            </el-menu-item>
            <el-menu-item index="my-consultations">
              <el-icon><Headset /></el-icon>
              <span>我的咨询</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧：详细内容 -->
        <div class="profile-content">
          <!-- 基本资料 Tab (keep existing) -->
          <el-card class="content-card" v-if="activeTab === 'info'">
            <!-- ... existing content ... -->
            <template #header>
              <div class="card-header">
                <h3>基本资料</h3>
                <el-button type="primary" v-if="!isEditing" @click="isEditing = true">编辑资料</el-button>
                <div v-else>
                  <el-button @click="cancelEdit">取消</el-button>
                  <el-button type="primary" @click="saveProfile" :loading="loading">保存</el-button>
                </div>
              </div>
            </template>

            <el-form 
              :model="form" 
              :rules="rules" 
              ref="formRef" 
              label-width="100px" 
              class="info-form"
              :disabled="!isEditing"
            >
              <el-form-item label="头像">
                <div class="avatar-uploader-wrapper" v-if="isEditing">
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :show-file-list="false"
                    :http-request="customUpload"
                    :before-upload="beforeAvatarUpload"
                  >
                    <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                  <span class="tip">点击修改头像</span>
                </div>
                <el-avatar v-else :size="60" :src="form.avatar || defaultAvatar" />
              </el-form-item>

              <el-form-item label="用户名">
                <el-input v-model="form.username" disabled />
                <span class="tip">用户名不可修改</span>
              </el-form-item>

              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" />
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" />
              </el-form-item>

              <el-form-item label="注册时间">
                <span>{{ formatTime(userStore.userInfo?.createTime) }}</span>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 修改密码 Tab (keep existing) -->
          <el-card class="content-card" v-if="activeTab === 'password'">
            <template #header>
              <h3>修改密码</h3>
            </template>
            <el-form 
              :model="pwdForm" 
              :rules="pwdRules" 
              ref="pwdFormRef" 
              label-width="100px"
              class="pwd-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePwd" :loading="loading">确认修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 实名认证 Tab -->
          <el-card class="content-card" v-if="activeTab === 'verify'">
            <template #header>
              <div class="card-header">
                <h3>实名认证</h3>
                <el-tag v-if="userStore.userInfo?.isVerified" type="success" size="large">
                  <el-icon><CircleCheckFilled /></el-icon> 已认证
                </el-tag>
                <el-tag v-else type="warning" size="large">
                  <el-icon><WarningFilled /></el-icon> 未认证
                </el-tag>
              </div>
            </template>

            <div class="verify-container">
              <el-alert
                title="为什么需要实名认证？"
                type="info"
                :closable="false"
                style="margin-bottom: 20px;"
              >
                <ul style="margin: 10px 0; padding-left: 20px; line-height: 2;">
                  <li>提升账号安全性，保护交易权益</li>
                  <li>获得更高的信用额度和交易权限</li>
                  <li><strong>认证成功即可获得 +20 信用分奖励</strong></li>
                  <li>享受平台优先推荐和专属服务</li>
                </ul>
              </el-alert>

              <template v-if="!userStore.userInfo?.isVerified">
                <el-form
                  :model="verifyForm"
                  :rules="verifyRules"
                  ref="verifyFormRef"
                  label-width="120px"
                  class="verify-form"
                >
                  <el-form-item label="学号" prop="studentId">
                    <el-input
                      v-model="verifyForm.studentId"
                      placeholder="请输入学号（10位数字，以2开头）"
                      maxlength="10"
                      clearable
                    >
                      <template #prepend>
                        <el-icon><CreditCard /></el-icon>
                      </template>
                    </el-input>
                    <span class="tip">例如：2024123456</span>
                  </el-form-item>

                  <el-form-item>
                    <el-button
                      type="primary"
                      size="large"
                      @click="handleVerifySubmit"
                      :loading="verifying"
                    >
                      <el-icon><Check /></el-icon>
                      提交认证
                    </el-button>
                    <span style="margin-left: 15px; color: #67c23a; font-size: 14px;">
                      <el-icon><Medal /></el-icon>
                      认证成功后立即获得 20 信用分
                    </span>
                  </el-form-item>
                </el-form>
              </template>

              <template v-else>
                <el-result
                  icon="success"
                  title="已完成实名认证"
                  sub-title="您已经通过实名认证，可以享受平台的所有服务"
                >
                  <template #extra>
                    <div class="verified-info">
                      <p><strong>学号：</strong>{{ userStore.userInfo?.studentId }}</p>
                      <p><strong>认证时间：</strong>{{ formatTime(userStore.userInfo?.updateTime) }}</p>
                      <el-button type="primary" @click="$router.push('/credit')">
                        查看我的信用
                      </el-button>
                    </div>
                  </template>
                </el-result>
              </template>
            </div>
          </el-card>

          <!-- 我的发布 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-products'" key="tab-products">
            <template #header>
              <div class="card-header">
                <h3>我的发布</h3>
                <el-button type="primary" icon="Plus" @click="$router.push('/publish')">发布新商品</el-button>
              </div>
            </template>
            
            <div class="product-list" v-loading="productLoading">
              <el-empty v-if="!myProducts || myProducts.length === 0" description="暂无发布商品" />
              <div v-else v-for="item in myProducts" :key="item.id" class="list-item">
                <el-image :src="getProductImage(item.images)" class="item-img" fit="cover" />
                <div class="item-info">
                  <h4 class="item-title" @click="$router.push(`/product/${item.id}`)">{{ item.title }}</h4>
                  <div class="item-meta">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="status-badge" :class="item.status">{{ getStatusText(item.status) }}</span>
                    <span class="date">发布于 {{ formatTime(item.createTime) }}</span>
                  </div>
                </div>
                <div class="item-actions">
                  <el-button 
                    v-if="item.status === 'ON_SALE'" 
                    size="small" 
                    type="warning" 
                    plain
                    @click="handleOffSale(item.id)"
                  >
                    下架
                  </el-button>
                  <el-button 
                    v-if="item.status === 'OFF_SALE'" 
                    size="small" 
                    type="success" 
                    plain
                    @click="handleOnSale(item.id)"
                  >
                    上架
                  </el-button>
                  <el-button size="small" @click="editProduct(item)">编辑</el-button>
                  <el-popconfirm title="确定要删除该商品吗？" @confirm="removeProduct(item.id)">
                    <template #reference>
                      <el-button size="small" type="danger" plain>删除</el-button>
                    </template>
                  </el-popconfirm>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的订单 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-orders'" key="tab-orders">
            <template #header>
              <div class="card-header">
                <h3>我的订单</h3>
                <el-radio-group v-model="orderType" size="small" @change="fetchOrders">
                  <el-radio-button label="buyer">我买到的</el-radio-button>
                  <el-radio-button label="seller">我卖出的</el-radio-button>
                </el-radio-group>
              </div>
            </template>

            <div class="order-list" v-loading="orderLoading">
              <el-empty v-if="!myOrders || myOrders.length === 0" description="暂无订单记录" />
              <div v-else v-for="order in myOrders" :key="order.id" class="order-card">
                <div class="order-header">
                  <span class="order-no">订单号：{{ order.orderNo }}</span>
                  <span class="order-status" :class="order.status">{{ getOrderStatusText(order.status) }}</span>
                </div>
                <div class="order-body">
                  <el-image :src="getProductImage(order.product?.images)" class="order-img" fit="cover" />
                  <div class="order-info">
                    <h4>{{ order.product?.title }}</h4>
                    <p>数量：{{ order.quantity }}</p>
                  </div>
                  <div class="order-price">
                    总价：<span>¥{{ order.totalAmount }}</span>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="date">{{ formatTime(order.createTime) }}</span>
                  <div class="actions">
                    <el-button v-if="canPay(order)" type="primary" size="small" @click="handlePay(order)">立即支付</el-button>
                    <el-button v-if="canShip(order)" type="primary" size="small" @click="handleShip(order)">发货</el-button>
                    <el-button v-if="canReceive(order)" type="success" size="small" @click="handleReceive(order)">确认收货</el-button>
                    <el-button v-if="canCancel(order)" size="small" @click="handleCancel(order)">取消订单</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的求购 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-wants'" key="tab-wants">
            <template #header>
              <div class="card-header">
                <h3>我的求购</h3>
                <el-button type="primary" size="small" @click="$router.push('/publish-want')">
                  <el-icon><Plus /></el-icon>
                  发布求购
                </el-button>
              </div>
            </template>

            <div class="want-list" v-loading="wantLoading">
              <el-empty v-if="!myWants || myWants.length === 0" description="暂无求购记录" />
              <div v-else v-for="want in myWants" :key="want.id" class="want-item">
                <div class="want-header">
                  <el-tag size="small" type="warning" effect="dark">求购</el-tag>
                  <h4 class="want-title">{{ want.title }}</h4>
                  <span class="want-status" :class="want.status">
                    {{ want.status === 'ACTIVE' ? '进行中' : '已关闭' }}
                  </span>
                </div>
                <p class="want-desc">{{ want.description }}</p>
                <div class="want-meta">
                  <div class="tags">
                    <el-tag v-for="tag in getWantTags(want.tags)" :key="tag" size="small" type="info">
                      {{ tag }}
                    </el-tag>
                  </div>
                  <div class="budget">
                    预算：<span class="price">¥{{ want.minPrice }} - {{ want.maxPrice }}</span>
                  </div>
                </div>
                <div class="want-footer">
                  <span class="date">发布于 {{ formatTime(want.createTime) }}</span>
                  <div class="actions">
                    <!-- 进行中的求购 -->
                    <template v-if="want.status === 'ACTIVE'">
                      <el-button size="small" @click="editWant(want)">编辑</el-button>
                      <el-button 
                        size="small" 
                        type="warning"
                        @click="closeWant(want.id)"
                      >
                        关闭求购
                      </el-button>
                    </template>
                    
                    <!-- 已关闭的求购 -->
                    <template v-else>
                      <el-button 
                        size="small" 
                        type="success"
                        @click="reopenWant(want.id)"
                      >
                        重新开启
                      </el-button>
                    </template>
                    
                    <!-- 删除按钮（所有状态都可以删除） -->
                    <el-popconfirm title="确定要删除该求购吗？" @confirm="removeWant(want.id)">
                      <template #reference>
                        <el-button size="small" type="danger" plain>删除</el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的出价 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-offers'" key="tab-offers">
            <template #header>
              <div class="card-header">
                <h3>我的出价</h3>
                <el-button type="primary" size="small" @click="$router.push('/wants')">
                  <el-icon><Plus /></el-icon>
                  去出价
                </el-button>
              </div>
            </template>

            <div class="offer-list" v-loading="offerLoading">
              <el-empty v-if="!myOffers || myOffers.length === 0" description="暂无出价记录" />
              <div v-else v-for="offer in myOffers" :key="offer.id" class="offer-item">
                <div class="offer-header">
                  <el-tag size="small" type="primary">出价</el-tag>
                  <h4 class="offer-title">{{ offer.wantTitle || '求购商品' }}</h4>
                  <el-tag size="small" :type="getOfferStatusType(offer.status)">
                    {{ getOfferStatusText(offer.status) }}
                  </el-tag>
                </div>
                <div class="offer-content">
                  <div class="offer-info">
                    <div class="info-row">
                      <span class="label">我的出价：</span>
                      <span class="price">¥{{ offer.price }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">商品描述：</span>
                      <span class="desc">{{ offer.description }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">联系方式：</span>
                      <span>{{ offer.contact }}</span>
                    </div>
                    <div class="info-row">
                      <span class="label">交易地点：</span>
                      <span>{{ offer.location }}</span>
                    </div>
                  </div>
                </div>
                <div class="offer-footer">
                  <span class="date">出价于 {{ formatTime(offer.createTime) }}</span>
                  <div class="actions">
                    <el-popconfirm 
                      title="确定要删除这条出价吗？删除后无法恢复" 
                      @confirm="removeOffer(offer.id)"
                      confirm-button-text="确定"
                      cancel-button-text="取消"
                    >
                      <template #reference>
                        <el-button size="small" type="danger" plain>删除</el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper" v-if="myOffers && myOffers.length > 0">
              <el-pagination
                v-model:current-page="offerPage"
                :page-size="offerPageSize"
                :total="offerTotal"
                layout="prev, pager, next"
                background
                @current-change="handleOfferPageChange"
              />
            </div>
          </el-card>

          <!-- 我的赠送 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-free'" key="tab-free">
            <template #header>
              <div class="card-header">
                <h3>我的赠送</h3>
                <el-button type="success" size="small" @click="$router.push('/publish-free')">
                  <el-icon><Present /></el-icon>
                  发布赠送
                </el-button>
              </div>
            </template>

            <div class="free-list" v-loading="freeLoading">
              <el-empty v-if="!myFree || myFree.length === 0" description="暂无赠送记录" />
              <div v-else v-for="item in myFree" :key="item.id" class="free-item">
                <el-image 
                  :src="getProductImage(item.images)" 
                  class="free-img" 
                  fit="cover"
                >
                  <template #error>
                    <div class="image-slot">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="free-info">
                  <h4 class="free-title">{{ item.title }}</h4>
                  <p class="free-desc">{{ item.description }}</p>
                  <div class="free-meta">
                    <el-tag size="small" type="success">{{ item.conditionDesc }}</el-tag>
                    <span class="location">
                      <el-icon><LocationFilled /></el-icon>
                      {{ item.location }}
                    </span>
                  </div>
                </div>
                <div class="free-status">
                  <span class="status-badge" :class="item.status">
                    {{ getFreeStatusText(item.status) }}
                  </span>
                  <div class="actions">
                    <!-- 可领取状态 -->
                    <template v-if="item.status === 'AVAILABLE'">
                      <el-button size="small" @click="editFree(item)">编辑</el-button>
                      <el-button 
                        size="small" 
                        type="success"
                        @click="markAsCompleted(item.id)"
                      >
                        标记已送出
                      </el-button>
                    </template>
                    
                    <!-- 已送出状态 -->
                    <template v-else>
                      <el-button 
                        size="small" 
                        type="primary"
                        @click="reopenFreeItem(item.id)"
                      >
                        重新上架
                      </el-button>
                    </template>
                    
                    <!-- 删除按钮 -->
                    <el-popconfirm title="确定要删除该赠送吗？" @confirm="removeFree(item.id)">
                      <template #reference>
                        <el-button size="small" type="danger" plain>删除</el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的通知 Tab -->
          <el-card class="content-card" v-if="activeTab === 'notifications'" key="tab-notifications">
            <template #header>
              <div class="card-header">
                <h3>我的通知 <span style="color: #909399; font-size: 14px; font-weight: normal;">({{ notifications.length }} 条)</span></h3>
                <div style="display: flex; gap: 10px;">
                  <el-button size="small" @click="loadNotifications">刷新</el-button>
                  <el-button v-if="notifications.length > 0" @click="markAllRead" :loading="markingAllRead">
                    全部标记已读
                  </el-button>
                </div>
              </div>
            </template>

            <div class="notifications-container">
              <el-empty v-if="notifications.length === 0" description="暂无通知">
                <el-button type="primary" @click="loadNotifications">刷新</el-button>
              </el-empty>

              <div v-else class="notifications-list">
                <div 
                  v-for="notification in notifications" 
                  :key="notification.id"
                  class="notification-item"
                  :class="{ unread: notification.isRead === 0 }"
                >
                  <div class="notification-header">
                    <div class="notification-title">
                      <el-icon v-if="notification.isRead === 0" class="unread-dot"><CircleCheckFilled /></el-icon>
                      <span class="title-text">{{ notification.title }}</span>
                      <el-tag v-if="notification.type === 'REPORT'" type="warning" size="small">举报</el-tag>
                      <el-tag v-else-if="notification.type === 'ORDER'" type="success" size="small">订单</el-tag>
                      <el-tag v-else type="info" size="small">系统</el-tag>
                    </div>
                    <span class="notification-time">{{ formatNotificationTime(notification.createTime) }}</span>
                  </div>
                  <div class="notification-content">{{ notification.content }}</div>
                  <div class="notification-actions">
                    <el-button 
                      v-if="notification.isRead === 0" 
                      size="small" 
                      @click="markAsRead(notification.id)"
                      text
                    >
                      标记已读
                    </el-button>
                    <el-button 
                      size="small" 
                      @click="deleteNotification(notification.id)"
                      type="danger"
                      text
                    >
                      删除
                    </el-button>
                  </div>
                </div>

                <!-- 分页 -->
                <div class="pagination-wrapper" v-if="notificationTotal > notificationPageSize">
                  <el-pagination
                    v-model:current-page="notificationPage"
                    v-model:page-size="notificationPageSize"
                    :page-sizes="[10, 20, 50]"
                    :total="notificationTotal"
                    layout="total, sizes, prev, pager, next"
                    @current-change="loadNotifications"
                    @size-change="loadNotifications"
                    background
                    small
                  />
                </div>
              </div>
            </div>
          </el-card>

          <!-- 我的咨询 Tab -->
          <el-card class="content-card" v-if="activeTab === 'my-consultations'" key="tab-consultations">
            <template #header>
              <div class="card-header">
                <h3>我的咨询</h3>
                <el-button size="small" @click="loadMyConsultations">刷新</el-button>
              </div>
            </template>

            <div class="consultations-container">
              <el-empty v-if="consultations.length === 0" description="暂无咨询记录" :image-size="120">
                <el-button type="primary" @click="openContactDialog">提交咨询</el-button>
              </el-empty>

              <div v-else class="consultations-list">
                <div 
                  v-for="consultation in consultations" 
                  :key="consultation.id"
                  class="consultation-item"
                >
                  <div class="consultation-header">
                    <div class="consultation-title">
                      <h4>{{ consultation.subject }}</h4>
                      <el-tag :type="getConsultationStatusType(consultation.status)" size="small">
                        {{ getConsultationStatusText(consultation.status) }}
                      </el-tag>
                      <el-tag :type="getConsultationCategoryType(consultation.category)" size="small">
                        {{ getConsultationCategoryText(consultation.category) }}
                      </el-tag>
                    </div>
                    <span class="consultation-time">{{ formatConsultationTime(consultation.createTime) }}</span>
                  </div>

                  <div class="consultation-content">
                    <div class="content-row">
                      <span class="label">咨询内容：</span>
                      <span class="value">{{ consultation.content }}</span>
                    </div>
                    <div class="content-row">
                      <span class="label">联系方式：</span>
                      <span class="value">{{ consultation.contactPhone }}</span>
                    </div>
                  </div>

                  <div class="consultation-reply" v-if="consultation.reply">
                    <div class="reply-header">
                      <el-icon class="reply-icon"><ChatDotSquare /></el-icon>
                      <span class="reply-title">客服回复</span>
                      <span class="reply-time">{{ formatConsultationTime(consultation.replyTime) }}</span>
                    </div>
                    <div class="reply-content">{{ consultation.reply }}</div>
                    <div class="reply-admin" v-if="consultation.adminName">
                      回复人：{{ consultation.adminName }}
                    </div>
                  </div>

                  <div class="consultation-actions">
                    <div v-if="!consultation.reply">
                      <el-text type="info" size="small">
                        <el-icon><Clock /></el-icon>
                        等待客服回复中...
                      </el-text>
                    </div>
                    <div v-else class="reply-actions">
                      <el-button type="primary" size="small" @click="handleFollowUp(consultation)">
                        <el-icon><ChatDotRound /></el-icon>
                        追问
                      </el-button>
                    </div>
                  </div>
                </div>

                <!-- 分页 -->
                <div class="pagination-wrapper" v-if="consultationTotal > consultationPageSize">
                  <el-pagination
                    v-model:current-page="consultationPage"
                    v-model:page-size="consultationPageSize"
                    :page-sizes="[10, 20, 50]"
                    :total="consultationTotal"
                    layout="total, sizes, prev, pager, next"
                    @current-change="loadMyConsultations"
                    @size-change="loadMyConsultations"
                    background
                    small
                  />
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 追问对话框 -->
    <el-dialog
      v-model="followUpDialogVisible"
      title="追问客服"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="follow-up-container">
        <!-- 原始咨询 -->
        <div class="original-consultation">
          <div class="section-title">原咨询</div>
          <div class="original-content">
            <div class="item">
              <span class="label">主题：</span>
              <span class="value">{{ currentFollowUp.subject }}</span>
            </div>
            <div class="item">
              <span class="label">内容：</span>
              <span class="value">{{ currentFollowUp.content }}</span>
            </div>
          </div>
          
          <!-- 客服回复 -->
          <div class="admin-reply" v-if="currentFollowUp.reply">
            <div class="section-title">客服回复</div>
            <div class="reply-text">{{ currentFollowUp.reply }}</div>
          </div>
        </div>
        
        <!-- 追问表单 -->
        <el-divider />
        <div class="section-title">继续提问</div>
        <el-form :model="followUpForm" ref="followUpFormRef">
          <el-form-item>
            <el-input
              v-model="followUpForm.content"
              type="textarea"
              :rows="5"
              placeholder="请输入您的追问内容..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="followUpDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFollowUp" :loading="followUpSubmitting">
          提交追问
        </el-button>
      </template>
    </el-dialog>

    <!-- 支付对话框 -->
    <el-dialog 
      v-model="paymentDialogVisible" 
      title="确认支付" 
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="payment-dialog" v-if="currentPayOrder">
        <!-- 订单信息 -->
        <div class="order-summary">
          <h4>订单信息</h4>
          <div class="summary-item">
            <span class="label">订单号：</span>
            <span class="value">{{ currentPayOrder.orderNo }}</span>
          </div>
          <div class="summary-item">
            <span class="label">商品名称：</span>
            <span class="value">{{ currentPayOrder.product?.title }}</span>
          </div>
          <div class="summary-item">
            <span class="label">购买数量：</span>
            <span class="value">{{ currentPayOrder.quantity }}</span>
          </div>
          <div class="summary-item total">
            <span class="label">应付金额：</span>
            <span class="value price">¥{{ currentPayOrder.totalAmount }}</span>
          </div>
        </div>

        <!-- 支付方式选择 -->
        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <el-radio-group v-model="selectedPayMethod" class="method-group">
            <el-radio label="alipay" class="method-item">
              <div class="method-content">
                <div class="method-icon alipay-icon">
                  <svg viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="45" fill="#1677FF"/>
                    <text x="50" y="65" font-size="50" fill="white" text-anchor="middle" font-family="Arial, sans-serif" font-weight="bold">支</text>
                  </svg>
                </div>
                <span class="method-name">支付宝</span>
              </div>
            </el-radio>
            <el-radio label="wechat" class="method-item">
              <div class="method-content">
                <div class="method-icon wechat-icon">
                  <svg viewBox="0 0 1024 1024">
                    <path d="M664.250054 368.541681c10.015098 0 19.892049 0.732687 29.67281 1.795902-26.647917-122.810047-159.358451-214.077703-310.826188-214.077703-169.353083 0-308.085774 114.232694-308.085774 259.274068 0 83.708494 46.165436 152.460344 123.281791 205.78483l-30.80868 91.730191 107.688651-53.455469c38.556573 7.53665 69.365252 15.308661 107.924012 15.308661 9.66111 0 19.230871-0.470721 28.752584-1.225921-6.025227-20.36584-9.66111-41.723264-9.66111-63.942420C402.328693 476.632491 517.908058 368.541681 664.250054 368.541681zM498.62897 285.87389c23.200398 0 38.556573 15.120372 38.556573 38.061874 0 22.846153-15.356175 38.270223-38.556573 38.270223-23.107581 0-46.307955-15.424069-46.307955-38.270223C452.321015 300.994262 475.522412 285.87389 498.62897 285.87389zM283.016307 362.206598c-23.107581 0-46.402606-15.424069-46.402606-38.270223 0-22.941502 23.295026-38.061874 46.402606-38.061874 23.081652 0 38.461924 15.120372 38.461924 38.061874C321.478231 346.782529 306.098982 362.206598 283.016307 362.206598z"></path>
                    <path d="M945.448458 606.151333c0-121.888048-123.258255-220.866816-261.683954-220.866816-146.57838 0-262.015505 98.979792-262.015505 220.866816 0 122.06508 115.4371 220.866816 262.015505 220.866816 30.66644 0 61.617929-7.609305 92.423387-15.260612l84.513836 45.786813-23.295026-76.209465C899.379213 735.776599 945.448458 674.90216 945.448458 606.151333zM598.803483 567.994292c-15.332732 0-30.80868-15.096836-30.80868-30.423479 0-15.190643 15.475948-30.80868 30.80868-30.80868 23.295026 0 38.461924 15.617014 38.461924 30.80868C637.265407 552.897456 622.098509 567.994292 598.803483 567.994292zM768.25071 567.994292c-15.213527 0-30.594031-15.096836-30.594031-30.423479 0-15.190643 15.379481-30.80868 30.594031-30.80868 23.107581 0 38.695429 15.617014 38.695429 30.80868C806.945116 552.897456 791.357268 567.994292 768.25071 567.994292z"></path>
                  </svg>
                </div>
                <span class="method-name">微信支付</span>
              </div>
            </el-radio>
            <el-radio label="balance" class="method-item">
              <div class="method-content">
                <el-icon class="method-icon-svg"><Wallet /></el-icon>
                <span class="method-name">余额支付</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 支付提示 -->
        <div class="payment-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>这是模拟支付，点击确认后将自动完成支付</span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelPay" :disabled="paying">取消</el-button>
          <el-button type="primary" @click="confirmPay" :loading="paying">
            {{ paying ? '支付中...' : '确认支付' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Profile.vue
// hangu: 0 行 | kelei: 49 行 | 本文件合计: 49 行
// ============================================================
import { ref, onMounted, reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, Plus, Goods, List, Wallet, InfoFilled, Present, Picture, LocationFilled, CreditCard, CircleCheckFilled, WarningFilled, Check, Medal, Bell, Money, Headset, ChatDotSquare, Clock, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { uploadFile } from '@/api/file'
import { updateUserInfo, updatePassword } from '@/api/user' 
import { getSellerProducts, deleteProduct, onSaleProduct, offSaleProduct } from '@/api/product'
import { getBuyerOrders, getSellerOrders, payOrder, shipOrder, completeOrder, cancelOrder } from '@/api/order'
import { getMyWants, closeWant as closeWantAPI, reopenWant as reopenWantAPI, deleteWant, getMyOffers } from '@/api/want'
import { getMyFree, completeFree, reopenFree as reopenFreeAPI, deleteFree } from '@/api/free'
import request from '@/utils/request'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const activeTab = ref('info')

// 用户统计数据
const userStats = ref({
  publishCount: 0,
  soldCount: 0,
  boughtCount: 0
})
const isEditing = ref(false)
const loading = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const verifyForm = reactive({
  studentId: ''
})

const formRef = ref(null)
const pwdFormRef = ref(null)
const verifyFormRef = ref(null)
const verifying = ref(false)

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const validateStudentId = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入学号'))
  } else if (!/^2\d{9}$/.test(value)) {
    callback(new Error('学号格式不正确，应为10位数字且以2开头'))
  } else {
    callback()
  }
}

const verifyRules = {
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { validator: validateStudentId, trigger: 'blur' }
  ]
}

const initForm = () => {
  const info = userStore.userInfo || {}
  form.username = info.username
  form.nickname = info.nickname
  form.phone = info.phone
  form.email = info.email
  form.avatar = info.avatar
}

// 通知相关数据
const notifications = ref([])
const unreadCount = ref(0)
const notificationPage = ref(1)
const notificationPageSize = ref(10)
const notificationTotal = ref(0)
const markingAllRead = ref(false)

// 咨询相关数据
const consultations = ref([])
const consultationPage = ref(1)
const consultationPageSize = ref(10)
const consultationTotal = ref(0)
const followUpDialogVisible = ref(false)
const currentFollowUp = ref({})
const followUpForm = reactive({
  content: ''
})
const followUpFormRef = ref(null)
const followUpSubmitting = ref(false)

// 加载通知列表
const loadNotifications = async () => {
  try {
    console.log('正在加载通知...')
    const res = await request.get('/notifications', {
      params: {
        // @contributor kelei - loadNotifications (49 行)
        page: notificationPage.value,
        size: notificationPageSize.value
      }
    })
    console.log('通知完整响应:', res)
    
    if (res) {
      // 后端返回可能是 { code: 200, data: {...} } 或直接是 { data: {...} }
      const responseData = res.data || res
      console.log('响应数据:', responseData)
      
      // 处理分页数据
      if (responseData.records || responseData.content) {
        notifications.value = responseData.records || responseData.content || []
        notificationTotal.value = responseData.total || responseData.totalElements || 0
      } else if (Array.isArray(responseData)) {
        // 如果直接返回数组
        notifications.value = responseData
        notificationTotal.value = responseData.length
      } else {
        notifications.value = []
        notificationTotal.value = 0
      }
      
      console.log('解析后的通知列表:', notifications.value)
      console.log('通知总数:', notificationTotal.value)
      
      // 更新未读数量
      unreadCount.value = notifications.value.filter(n => n.isRead === 0).length
      console.log('未读数量:', unreadCount.value)
    } else {
      console.log('响应为空')
      notifications.value = []
      notificationTotal.value = 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    console.error('错误详情:', error.response)
    ElMessage.error('加载通知失败：' + (error.response?.data?.message || error.message))
    notifications.value = []
    notificationTotal.value = 0
  }
}

// 标记为已读
const markAsRead = async (id) => {
  try {
    const res = await request.put(`/notifications/${id}/read`)
    console.log('标记已读响应:', res)
    if (res && (res.code === 200 || res.success || res.data)) {
      ElMessage.success('已标记为已读')
      loadNotifications()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败：' + (error.response?.data?.message || error.message))
  }
}

// 全部标记已读
const markAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    markingAllRead.value = true
    const res = await request.put('/notifications/read-all')
    console.log('全部标记已读响应:', res)
    if (res && (res.code === 200 || res.success || res.data)) {
      ElMessage.success('已全部标记为已读')
      loadNotifications()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('全部标记已读失败:', error)
      ElMessage.error('操作失败：' + (error.response?.data?.message || error.message))
    }
  } finally {
    markingAllRead.value = false
  }
}

// 删除通知
const deleteNotification = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/notifications/${id}`)
    console.log('删除通知响应:', res)
    if (res && (res.code === 200 || res.success || res.data)) {
      ElMessage.success('删除成功')
      loadNotifications()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error)
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }
}

// 格式化通知时间
const formatNotificationTime = (timeStr) => {
  if (!timeStr) return ''
  
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 1分钟内
  if (diff < 60000) {
    return '刚刚'
  }
  // 1小时内
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  }
  // 今天
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  // 昨天
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  // 更早
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const handleSelect = (index) => {
  activeTab.value = index
  isEditing.value = false
  
  // 切换到通知Tab时加载通知
  if (index === 'notifications') {
    loadNotifications()
  }
  
  // 切换到咨询Tab时加载咨询
  if (index === 'my-consultations') {
    loadMyConsultations()
  }
}

const cancelEdit = () => {
  isEditing.value = false
  initForm()
}

const customUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  formData.append('type', 'avatar')
  
  try {
    const res = await uploadFile(formData)
    if (res.code === 200) {
      const url = res.data.startsWith('/api') ? res.data : '/api' + res.data
      form.avatar = url
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传出错')
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isJPG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isJPG) return ElMessage.error('头像只能是 JPG/PNG 格式!') && false
  if (!isLt2M) return ElMessage.error('头像大小不能超过 2MB!') && false
  return true
}

const saveProfile = async () => {
  loading.value = true
  try {
    const res = await updateUserInfo(form)
    if (res.code === 200) {
      userStore.setUserInfo(res.data)
      ElMessage.success('保存成功')
      isEditing.value = false
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('保存出错')
  } finally {
    loading.value = false
  }
}

const handleUpdatePwd = async () => {
  await pwdFormRef.value.validate()
  loading.value = true
  try {
    const res = await updatePassword(pwdForm)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('修改出错')
  } finally {
    loading.value = false
  }
}

// 实名认证提交
const handleVerifySubmit = async () => {
  try {
    await verifyFormRef.value.validate()
  } catch (error) {
    return
  }

  verifying.value = true
  try {
    const res = await request.post('/users/verify', {
      studentId: verifyForm.studentId
    })
    
    if (res.code === 200) {
      ElMessage.success({
        message: '认证成功，获得20信用分！',
        duration: 3000,
        showClose: true
      })
      // 刷新用户信息
      await userStore.fetchUserInfo()
      // 清空表单
      verifyForm.studentId = ''
    } else {
      ElMessage.error(res.message || '认证失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '认证失败')
  } finally {
    verifying.value = false
  }
}

// 我的发布相关
const myProducts = ref([])
const productLoading = ref(false)

const fetchMyProducts = async () => {
  productLoading.value = true
  try {
    const sellerId = userStore.userInfo?.id
    const res = await getSellerProducts(sellerId, { page: 0, size: 100 })
    myProducts.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('获取我的发布失败:', error)
    ElMessage.error('获取我的发布失败')
  } finally {
    productLoading.value = false
  }
}

const editProduct = (product) => {
  router.push(`/publish?id=${product.id}`)
}

const removeProduct = async (id) => {
  try {
    const res = await deleteProduct(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchMyProducts()
      loadUserStats() // 刷新统计数据
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除商品失败:', error)
    ElMessage.error('删除失败')
  }
}

const getStatusText = (status) => {
  const map = {
    'ON_SALE': '在售',
    'SOLD': '已售',
    'OFF_SALE': '下架'
  }
  return map[status] || status
}

// 上架商品
const handleOnSale = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要上架该商品吗？上架后商品将在首页展示。',
      '上架确认',
      {
        confirmButtonText: '确定上架',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const res = await onSaleProduct(id)
    if (res.code === 200) {
      ElMessage.success('上架成功')
      fetchMyProducts()
    } else {
      ElMessage.error(res.message || '上架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('上架商品失败:', error)
      ElMessage.error('上架失败')
    }
  }
}

// 下架商品
const handleOffSale = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要下架该商品吗？下架后商品将不在首页展示。',
      '下架确认',
      {
        confirmButtonText: '确定下架',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await offSaleProduct(id)
    if (res.code === 200) {
      ElMessage.success('下架成功')
      fetchMyProducts()
    } else {
      ElMessage.error(res.message || '下架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架商品失败:', error)
      ElMessage.error('下架失败')
    }
  }
}

// 我的订单相关
const myOrders = ref([])
const orderLoading = ref(false)
const orderType = ref('buyer')

const fetchOrders = async () => {
  orderLoading.value = true
  try {
    const params = {
      [orderType.value + 'Id']: userStore.userInfo?.id,
      page: 0,
      size: 100
    }
    const res = orderType.value === 'buyer' 
      ? await getBuyerOrders(params)
      : await getSellerOrders(params)
    myOrders.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    orderLoading.value = false
  }
}

const getOrderStatusText = (status) => {
  const map = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const canPay = (order) => order.status === 'PENDING' && orderType.value === 'buyer'
const canShip = (order) => order.status === 'PAID' && orderType.value === 'seller'
const canReceive = (order) => order.status === 'SHIPPED' && orderType.value === 'buyer'
const canCancel = (order) => order.status === 'PENDING'

// 支付相关
const paymentDialogVisible = ref(false)
const currentPayOrder = ref(null)
const selectedPayMethod = ref('alipay')
const paying = ref(false)

const handlePay = (order) => {
  currentPayOrder.value = order
  selectedPayMethod.value = 'alipay'
  paymentDialogVisible.value = true
}

const confirmPay = async () => {
  if (!selectedPayMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  paying.value = true
  
  try {
    // 模拟支付处理延迟
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const res = await payOrder(currentPayOrder.value.id)
    if (res.code === 200) {
      ElMessage.success('支付成功！')
      paymentDialogVisible.value = false
      fetchOrders()
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

const cancelPay = () => {
  paymentDialogVisible.value = false
  currentPayOrder.value = null
}

const handleShip = async (order) => {
  try {
    await ElMessageBox.confirm('确认发货？', '发货确认')
    const res = await shipOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('发货成功')
      fetchOrders()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }
}

const handleReceive = async (order) => {
  try {
    await ElMessageBox.confirm('确认收货？', '确认收货')
    const res = await completeOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('确认收货成功')
      fetchOrders()
      loadUserStats() // 刷新统计数据
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单')
    const res = await cancelOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      fetchOrders()
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

// 我的求购相关
const myWants = ref([])
const wantLoading = ref(false)
const myOffers = ref([])
const offerLoading = ref(false)
const offerPage = ref(1)
const offerPageSize = ref(10)
const offerTotal = ref(0)

const fetchMyWants = async () => {
  wantLoading.value = true
  try {
    const res = await getMyWants({ page: 0, size: 100 })
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      myWants.value = Array.isArray(list) ? list : []
    }
  } catch (error) {
    console.error('获取我的求购失败:', error)
  } finally {
    wantLoading.value = false
  }
}

const getWantTags = (tags) => {
  if (!tags) return []
  return tags.split(',').filter(t => t)
}

const editWant = (want) => {
  router.push(`/publish-want?id=${want.id}`)
}

const closeWant = async (id) => {
  try {
    await ElMessageBox.confirm('确定要关闭该求购吗？关闭后将不再显示在求购列表中。', '关闭求购', {
      confirmButtonText: '确定关闭',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await closeWantAPI(id)
    if (res && res.code === 200) {
      ElMessage.success('已关闭求购')
      fetchMyWants()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭求购失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const reopenWant = async (id) => {
  try {
    await ElMessageBox.confirm('确定要重新开启该求购吗？开启后将重新显示在求购列表中。', '开启求购', {
      confirmButtonText: '确定开启',
      cancelButtonText: '取消',
      type: 'success'
    })
    const res = await reopenWantAPI(id)
    if (res && res.code === 200) {
      ElMessage.success('已重新开启求购')
      fetchMyWants()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开启求购失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const removeWant = async (id) => {
  try {
    const res = await deleteWant(id)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      fetchMyWants()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除求购失败:', error)
    ElMessage.error('删除失败')
  }
}

// 我的出价相关
const fetchMyOffers = async () => {
  offerLoading.value = true
  try {
    const res = await getMyOffers({ page: offerPage.value - 1, size: offerPageSize.value })
    if (res && res.data) {
      const pageData = res.data.records || res.data.content || []
      myOffers.value = Array.isArray(pageData) ? pageData : []
      offerTotal.value = res.data.total || res.data.totalElements || 0
    }
  } catch (error) {
    console.error('获取我的出价失败:', error)
    ElMessage.error('获取出价列表失败')
  } finally {
    offerLoading.value = false
  }
}

const handleOfferPageChange = () => {
  fetchMyOffers()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 出价状态文本
const getOfferStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 出价状态类型
const getOfferStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'success',
    'REJECTED': 'info'
  }
  return typeMap[status] || 'info'
}

// 删除出价
const removeOffer = async (id) => {
  try {
    const res = await request.delete(`/want-offers/${id}`)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      fetchMyOffers()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除出价失败:', error)
    ElMessage.error('删除失败')
  }
}

// 我的赠送相关
const myFree = ref([])
const freeLoading = ref(false)

const fetchMyFree = async () => {
  freeLoading.value = true
  try {
    const res = await getMyFree({ page: 0, size: 100 })
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      myFree.value = Array.isArray(list) ? list : []
    }
  } catch (error) {
    console.error('获取我的赠送失败:', error)
  } finally {
    freeLoading.value = false
  }
}

const editFree = (item) => {
  router.push(`/publish-free?id=${item.id}`)
}

const markAsCompleted = async (id) => {
  try {
    await ElMessageBox.confirm('确定标记为已送出吗？标记后将不再显示在赠送列表中。', '标记已送出', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    const res = await completeFree(id)
    if (res && res.code === 200) {
      ElMessage.success('已标记为送出')
      fetchMyFree()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const reopenFreeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定重新上架吗？上架后将重新显示在赠送列表中。', '重新上架', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    const res = await reopenFreeAPI(id)
    if (res && res.code === 200) {
      ElMessage.success('已重新上架')
      fetchMyFree()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重新上架失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const removeFree = async (id) => {
  try {
    const res = await deleteFree(id)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      fetchMyFree()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除赠送失败:', error)
    ElMessage.error('删除失败')
  }
}

const getFreeStatusText = (status) => {
  const map = {
    'AVAILABLE': '可领取',
    'RESERVED': '已预约',
    'COMPLETED': '已送出'
  }
  return map[status] || status
}

const getRoleText = (role) => {
  const map = { 'USER': '普通用户', 'SELLER': '卖家', 'ADMIN': '管理员' }
  return map[role] || role
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 处理图片URL
const getProductImage = (images) => {
  if (!images) return ''
  
  let imageUrl = ''
  if (Array.isArray(images)) {
    imageUrl = images[0] || ''
  } else if (typeof images === 'string') {
    imageUrl = images.split(',')[0] || ''
  }
  
  if (!imageUrl) return ''
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  if (imageUrl.startsWith('/api/')) {
    return imageUrl
  }
  if (!imageUrl.startsWith('/')) {
    imageUrl = '/' + imageUrl
  }
  return '/api' + imageUrl
}

// 监听activeTab变化，加载对应数据
watch(activeTab, (newVal) => {
  if (newVal === 'my-products') {
    fetchMyProducts()
  } else if (newVal === 'my-orders') {
    fetchOrders()
  } else if (newVal === 'my-wants') {
    fetchMyWants()
  } else if (newVal === 'my-offers') {
    fetchMyOffers()
  } else if (newVal === 'my-free') {
    fetchMyFree()
  }
})

// 加载我的咨询列表
const loadMyConsultations = async () => {
  try {
    const res = await request.get('/customer-service/my', {
      params: {
        page: consultationPage.value - 1,
        size: consultationPageSize.value
      }
    })
    
    if (res && res.data) {
      consultations.value = res.data.records || []
      consultationTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载咨询列表失败:', error)
    ElMessage.error('加载咨询列表失败')
  }
}

// 打开联系客服对话框
const openContactDialog = () => {
  // 跳转到首页并打开客服对话框
  router.push('/')
}

// 获取咨询状态类型
const getConsultationStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PROCESSING': 'info',
    'REPLIED': 'success',
    'CLOSED': ''
  }
  return map[status] || ''
}

// 获取咨询状态文本
const getConsultationStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'REPLIED': '已回复',
    'CLOSED': '已关闭'
  }
  return map[status] || status
}

// 获取咨询类型样式
const getConsultationCategoryType = (category) => {
  const map = {
    'GENERAL': '',
    'COMPLAINT': 'danger',
    'SUGGESTION': 'success',
    'TECHNICAL': 'warning'
  }
  return map[category] || ''
}

// 获取咨询类型文本
const getConsultationCategoryText = (category) => {
  const map = {
    'GENERAL': '一般咨询',
    'COMPLAINT': '投诉建议',
    'SUGGESTION': '功能建议',
    'TECHNICAL': '技术问题'
  }
  return map[category] || category
}

// 格式化咨询时间
const formatConsultationTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

// 处理追问
const handleFollowUp = (consultation) => {
  currentFollowUp.value = consultation
  followUpForm.content = ''
  followUpDialogVisible.value = true
}

// 提交追问
const submitFollowUp = async () => {
  if (!followUpForm.content || followUpForm.content.trim().length < 5) {
    ElMessage.warning('请输入至少5个字符的追问内容')
    return
  }
  try {
    followUpSubmitting.value = true
    // 创建一个新的咨询，标记为追问
    const consultation = {
      userName: userStore.userInfo?.nickname || userStore.userInfo?.username,
      contactPhone: userStore.userInfo?.phone || currentFollowUp.value.contactPhone,
      contactEmail: userStore.userInfo?.email || currentFollowUp.value.contactEmail,
      category: currentFollowUp.value.category,
      subject: `[续问] ${currentFollowUp.value.subject}`,
      content: `**原问题：**\n${currentFollowUp.value.content}\n\n**客服回复：**\n${currentFollowUp.value.reply}\n\n**继续追问：**\n${followUpForm.content}`
    }
    
    await request.post('/customer-service/submit', consultation)
    
    ElMessage.success('追问已提交，客服会尽快回复')
    followUpDialogVisible.value = false
    
    // 刷新咨询列表
    loadMyConsultations()
  } catch (error) {
    console.error('提交追问失败:', error)
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    followUpSubmitting.value = false
  }
}

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    const res = await request.get('/users/stats')
    if (res && res.data) {
      userStats.value = {
        publishCount: res.data.publishCount || 0,
        soldCount: res.data.soldCount || 0,
        boughtCount: res.data.boughtCount || 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  if (route.query.tab) {
    activeTab.value = route.query.tab
    // 如果是通知标签，加载通知
    if (route.query.tab === 'notifications') {
      loadNotifications()
    }
  }
  initForm()
  // 加载用户统计数据
  loadUserStats()
  // 初始加载数据
  if (activeTab.value === 'my-products') {
    fetchMyProducts()
  } else if (activeTab.value === 'my-orders') {
    fetchOrders()
  }
})
</script>

<style scoped>
.profile-page {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
  padding: 20px 0;
}

.main-container {
  width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

.profile-layout {
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 280px;
}

.user-card {
  text-align: center;
  margin-bottom: 20px;
  border-radius: 8px;
}

.avatar-box {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}

.role-tag {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #3e6bda;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.nickname {
  font-size: 18px;
  color: #333;
  margin: 0 0 4px 0;
}

.username {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.stat-item .num {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-item .label {
  font-size: 12px;
  color: #999;
}

.profile-menu {
  border-radius: 8px;
  border: none;
  overflow: hidden;
}

.profile-content {
  flex: 1;
}

.content-card {
  border-radius: 8px;
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.info-form {
  max-width: 500px;
  padding-top: 20px;
}

.tip {
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}

.avatar-uploader-wrapper {
  display: flex;
  align-items: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 80px;
  height: 80px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #3e6bda;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 80px;
  height: 80px;
  display: block;
  object-fit: cover;
}

.pwd-form {
  max-width: 400px;
  padding-top: 20px;
}

/* 我的发布样式 */
.product-list {
  min-height: 300px;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.list-item:hover {
  background: #fafafa;
}

.item-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  margin-right: 16px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-title:hover {
  color: #3e6bda;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}

.item-meta .price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-badge.ON_SALE {
  background: #e6f7ff;
  color: #1890ff;
}

.status-badge.SOLD {
  background: #f0f0f0;
  color: #999;
}

.status-badge.OFF_SALE {
  background: #fff1f0;
  color: #ff4d4f;
}

.item-meta .date {
  color: #999;
  font-size: 12px;
}

.item-actions {
  display: flex;
  gap: 8px;
}

/* 我的订单样式 */
.order-list {
  min-height: 300px;
}

.order-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.order-status.PENDING {
  background: #fff7e6;
  color: #fa8c16;
}

.order-status.PAID {
  background: #e6f7ff;
  color: #1890ff;
}

.order-status.SHIPPED {
  background: #f6ffed;
  color: #52c41a;
}

.order-status.COMPLETED {
  background: #f0f0f0;
  color: #999;
}

.order-status.CANCELLED {
  background: #fff1f0;
  color: #ff4d4f;
}

.order-body {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 16px;
}

.order-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  flex-shrink: 0;
}

.order-info {
  flex: 1;
}

.order-info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.order-info p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.order-price {
  font-size: 14px;
  color: #666;
}

.order-price span {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: bold;
  margin-left: 8px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}

.order-footer .date {
  font-size: 12px;
  color: #999;
}

.order-footer .actions {
  display: flex;
  gap: 8px;
}

/* 支付对话框样式 */
.payment-dialog {
  padding: 10px 0;
}

.order-summary {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.order-summary h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

.summary-item .label {
  color: #666;
}

.summary-item .value {
  color: #333;
  font-weight: 500;
}

.summary-item.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ddd;
}

.summary-item.total .label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.summary-item.total .price {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
}

.payment-methods {
  margin-bottom: 20px;
}

.payment-methods h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.method-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  width: 100%;
  margin: 0 !important;
  padding: 18px 20px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  transition: all 0.3s;
}

.method-item:hover {
  border-color: #3e6bda;
  background: #f0f5ff;
}

.method-item.is-checked {
  border-color: #3e6bda;
  background: #f0f5ff;
}

.method-content {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: 8px;
}

.method-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.method-icon svg {
  width: 100%;
  height: 100%;
  max-width: 36px;
  max-height: 36px;
}

.wechat-icon svg path {
  fill: #07C160;
}

.method-icon-svg {
  font-size: 36px;
  color: #FF9500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.method-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.payment-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
  color: #ad6800;
  font-size: 13px;
}

.payment-tips .el-icon {
  font-size: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 我的求购样式 */
.want-list {
  min-height: 300px;
}

.want-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.want-item:hover {
  background: #fafafa;
}

.want-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.want-title {
  flex: 1;
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.want-status {
  font-size: 13px;
  padding: 2px 12px;
  border-radius: 12px;
}

.want-status.ACTIVE {
  background: #e6f7ff;
  color: #1890ff;
}

.want-status.CLOSED {
  background: #f5f5f5;
  color: #999;
}

.want-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.want-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.want-meta .tags {
  display: flex;
  gap: 8px;
}

.want-meta .budget {
  font-size: 14px;
  color: #666;
}

.want-meta .price {
  color: #ff6b00;
  font-weight: 600;
  font-size: 16px;
}

.want-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.want-footer .date {
  font-size: 12px;
  color: #999;
}

.want-footer .actions {
  display: flex;
  gap: 8px;
}

/* 我的出价样式 */
.offer-list {
  min-height: 300px;
}

.offer-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.offer-item:hover {
  background: #fafafa;
}

.offer-item:last-child {
  border-bottom: none;
}

.offer-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.offer-title {
  flex: 1;
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.offer-content {
  margin-bottom: 12px;
}

.offer-info .info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 14px;
}

.offer-info .info-row .label {
  width: 90px;
  color: #666;
  flex-shrink: 0;
}

.offer-info .info-row .price {
  color: #ff6b00;
  font-weight: 600;
  font-size: 18px;
}

.offer-info .info-row .desc {
  flex: 1;
  color: #333;
  line-height: 1.6;
}

.offer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.offer-footer .date {
  font-size: 12px;
  color: #999;
}

.offer-footer .actions {
  display: flex;
  gap: 8px;
}

/* 我的赠送样式 */
.free-list {
  min-height: 300px;
}

.free-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.free-item:hover {
  background: #fafafa;
}

.free-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  flex-shrink: 0;
}

.free-info {
  flex: 1;
  min-width: 0;
}

.free-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.free-desc {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.free-meta {
  display: flex;
  gap: 16px;
  align-items: center;
}

.free-meta .location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.free-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 200px;
}

.status-badge {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
}

.status-badge.AVAILABLE {
  background: #f0f9ff;
  color: #0284c7;
}

.status-badge.COMPLETED {
  background: #f0fdf4;
  color: #16a34a;
}

.free-status .actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* 通知样式 */
.notifications-container {
  min-height: 400px;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  background: #fff;
  transition: all 0.3s;
}

.notification-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transform: translateX(2px);
}

.notification-item.unread {
  background: #f0f9ff;
  border-color: #409eff;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.unread-dot {
  color: #409eff;
  font-size: 12px;
}

.title-text {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.notification-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.notification-badge-menu {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 咨询样式 */
.consultations-container {
  min-height: 400px;
}

.consultations-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consultation-item {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.consultation-item:hover {
  background: #f5f7fa;
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.consultation-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.consultation-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.consultation-title h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.consultation-time {
  font-size: 13px;
  color: #909399;
}

.consultation-content {
  margin: 12px 0;
  padding: 12px;
  background: white;
  border-radius: 6px;
}

.content-row {
  display: flex;
  margin-bottom: 8px;
  line-height: 1.6;
}

.content-row:last-child {
  margin-bottom: 0;
}

.content-row .label {
  font-weight: 600;
  color: #606266;
  width: 90px;
  flex-shrink: 0;
}

.content-row .value {
  flex: 1;
  color: #606266;
}

.consultation-reply {
  margin-top: 12px;
  padding: 16px;
  background: #e7f7ff;
  border-radius: 6px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-weight: 600;
  color: #409eff;
}

.reply-icon {
  font-size: 18px;
}

.reply-title {
  font-size: 14px;
}

.reply-time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.reply-content {
  color: #303133;
  line-height: 1.6;
  margin-bottom: 8px;
}

.reply-admin {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.consultation-actions {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;
}

.reply-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 追问对话框 */
.follow-up-container {
  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }
  
  .original-consultation {
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 16px;
  }
  
  .original-content {
    .item {
      margin-bottom: 8px;
      display: flex;
      
      .label {
        font-weight: 600;
        color: #606266;
        width: 70px;
        flex-shrink: 0;
      }
      
      .value {
        flex: 1;
        color: #606266;
        line-height: 1.6;
      }
    }
    
    .item:last-child {
      margin-bottom: 0;
    }
  }
  
  .admin-reply {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #e4e7ed;
    
    .reply-text {
      padding: 12px;
      background: #e7f7ff;
      border-radius: 6px;
      color: #303133;
      line-height: 1.6;
    }
  }
}
</style>










