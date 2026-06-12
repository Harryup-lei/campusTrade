<template>
  <div class="data-charts-container">
    <el-page-header @back="$router.back()" content="数据报表" />
    
    <!-- 时间范围选择 -->
    <el-card class="filter-card">
      <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
        <el-radio-button label="7">最近7天</el-radio-button>
        <el-radio-button label="15">最近15天</el-radio-button>
        <el-radio-button label="30">最近30天</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 交易趋势 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="title">
                <el-icon><TrendCharts /></el-icon>
                交易趋势分析
              </span>
            </div>
          </template>
          <div ref="tradeTrendChart" class="chart" style="height: 400px"></div>
        </el-card>
      </el-col>

      <!-- 商品分类占比 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="title">
                <el-icon><PieChart /></el-icon>
                商品分类分布
              </span>
            </div>
          </template>
          <div ref="categoryChart" class="chart" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 热门商品排行 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="title">
                <el-icon><DataLine /></el-icon>
                热门商品排行
              </span>
            </div>
          </template>
          <div ref="hotProductsChart" class="chart" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 用户增长趋势 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="title">
                <el-icon><User /></el-icon>
                用户增长趋势
              </span>
            </div>
          </template>
          <div ref="userGrowthChart" class="chart" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/DataCharts.vue
// hangu: 302 行 | kelei: 0 行 | 本文件合计: 302 行
// ============================================================
import { ref, onMounted, onBeforeUnmount } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { TrendCharts, PieChart, DataLine, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const timeRange = ref('7')
const tradeTrendChart = ref(null)
const categoryChart = ref(null)
const hotProductsChart = ref(null)
const userGrowthChart = ref(null)

let tradeTrendChartInstance = null
let categoryChartInstance = null
let hotProductsChartInstance = null
let userGrowthChartInstance = null

// 初始化交易趋势图表
const initTradeTrendChart = async () => {
  try {
    const res = await request.get('/statistics/charts/trade-trend', {
      params: { days: timeRange.value }
    })
    // @contributor hangu - initTradeTrendChart (105 行)
    
    if (!tradeTrendChartInstance) {
      tradeTrendChartInstance = echarts.init(tradeTrendChart.value)
    }
    
    const option = {
      title: {
        text: '交易数据趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        }
      },
      legend: {
        data: ['订单数量', '交易金额'],
        top: 30
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: res.data.dates
      },
      yAxis: [
        {
          type: 'value',
          name: '订单数量',
          position: 'left',
          axisLine: {
            show: true,
            lineStyle: {
              color: '#5470C6'
            }
          }
        },
        {
          type: 'value',
          name: '金额（元）',
          position: 'right',
          axisLine: {
            show: true,
            lineStyle: {
              color: '#91CC75'
            }
          }
        }
      ],
      series: [
        {
          name: '订单数量',
          type: 'line',
          smooth: true,
          data: res.data.orderCounts,
          yAxisIndex: 0,
          itemStyle: {
            color: '#5470C6'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(84, 112, 198, 0.3)'
              }, {
                offset: 1, color: 'rgba(84, 112, 198, 0.1)'
              }]
            }
          }
        },
        {
          name: '交易金额',
          type: 'line',
          smooth: true,
          data: res.data.amounts,
          yAxisIndex: 1,
          itemStyle: {
            color: '#91CC75'
          }
        }
      ]
    }
    
    tradeTrendChartInstance.setOption(option)
  } catch (error) {
    console.error('获取交易趋势数据失败:', error)
    ElMessage.error('获取交易趋势数据失败')
  }
}

// 初始化分类占比图表
const initCategoryChart = async () => {
  try {
    const res = await request.get('/statistics/charts/category-distribution')
    
    if (!categoryChartInstance) {
      categoryChartInstance = echarts.init(categoryChart.value)
    // @contributor hangu - initCategoryChart (59 行)
    }
    
    const option = {
      title: {
        text: '商品分类占比',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center'
      },
      series: [
        {
          name: '商品数量',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: res.data
        }
      ]
    }
    
    categoryChartInstance.setOption(option)
  } catch (error) {
    console.error('获取分类分布数据失败:', error)
    ElMessage.error('获取分类分布数据失败')
  }
}

// 初始化热门商品图表
const initHotProductsChart = async () => {
  try {
    const res = await request.get('/statistics/charts/hot-products', {
      params: { limit: 10 }
    })
    
    if (!hotProductsChartInstance) {
      // @contributor hangu - initHotProductsChart (68 行)
      hotProductsChartInstance = echarts.init(hotProductsChart.value)
    }
    const option = {
      title: {
        text: '热门商品排行（浏览量）',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        name: '浏览量'
      },
      yAxis: {
        type: 'category',
        data: res.data.names
      },
      series: [
        {
          name: '浏览量',
          type: 'bar',
          data: res.data.viewCounts,
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [{
                offset: 0, color: '#EE6666'
              }, {
                offset: 1, color: '#FAC858'
              }]
            }
          },
          label: {
            show: true,
            position: 'right'
          }
        }
      ]
    }
    
    hotProductsChartInstance.setOption(option)
  } catch (error) {
    console.error('获取热门商品数据失败:', error)
    ElMessage.error('获取热门商品数据失败')
  }
}

// 初始化用户增长图表
const initUserGrowthChart = async () => {
  try {
    const res = await request.get('/statistics/charts/user-growth', {
      params: { days: timeRange.value }
    })
    
    if (!userGrowthChartInstance) {
      userGrowthChartInstance = echarts.init(userGrowthChart.value)
    // @contributor hangu - initUserGrowthChart (70 行)
    }
    
    const option = {
      title: {
        text: '用户增长趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: res.data.dates
      },
      yAxis: {
        type: 'value',
        name: '用户总数'
      },
      series: [
        {
          name: '用户数量',
          type: 'line',
          smooth: true,
          data: res.data.userCounts,
          itemStyle: {
            color: '#73C0DE'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(115, 192, 222, 0.5)'
              }, {
                offset: 1, color: 'rgba(115, 192, 222, 0.1)'
              }]
            }
          }
        }
      ]
    }
    
    userGrowthChartInstance.setOption(option)
  } catch (error) {
    console.error('获取用户增长数据失败:', error)
    ElMessage.error('获取用户增长数据失败')
  }
}

// 时间范围变化
const handleTimeRangeChange = () => {
  initTradeTrendChart()
  initUserGrowthChart()
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  tradeTrendChartInstance?.resize()
  categoryChartInstance?.resize()
  hotProductsChartInstance?.resize()
  userGrowthChartInstance?.resize()
}

onMounted(() => {
  initTradeTrendChart()
  initCategoryChart()
  initHotProductsChart()
  initUserGrowthChart()
  
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  tradeTrendChartInstance?.dispose()
  categoryChartInstance?.dispose()
  hotProductsChartInstance?.dispose()
  userGrowthChartInstance?.dispose()
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.data-charts-container {
  padding: 20px;
  
  .filter-card {
    margin: 20px 0;
    
    .el-radio-group {
      width: 100%;
      display: flex;
      justify-content: center;
    }
  }
  
  .chart-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        
        .el-icon {
          font-size: 18px;
          color: #409EFF;
        }
      }
    }
    
    .chart {
      width: 100%;
    }
  }
}

:deep(.el-page-header) {
  margin-bottom: 20px;
}
</style>










