import numpy as np
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties

# 设置中文字体
font = FontProperties(fname=r'C:\Windows\Fonts\simhei.ttf')

# 数据准备
models = ['DeepSeek-R1-Distill-Qwen-7B', '微调Deepseek的摘要模型']
metrics = ['ROUGE-1', 'ROUGE-2', 'ROUGE-3', 'ROUGE-4']
values = np.array([
    [18.91, 8.46, 4.15, 2.15],
    [32.30, 9.14, 4.94, 3.51]
])

# 计算角度
angles = np.linspace(0, 2*np.pi, len(metrics), endpoint=False)
angles = np.concatenate((angles, [angles[0]]))

# 创建图表
plt.figure(figsize=(12, 12))
ax = plt.subplot(111, projection='polar')
ax.set_facecolor('white')

# 设置主题颜色
colors = ['#8A2BE2', '#9370DB']  # 两种紫色
grid_color = '#E6E6FA'

# 设置网格
ax.grid(True, color=grid_color, linestyle='-', linewidth=1)
ax.set_ylim(0, 35)

# 设置径向刻度（y轴）
ax.set_yticks(np.arange(0, 36, 5))
ax.set_yticklabels([f'{i}' for i in range(0, 36, 5)], fontsize=10)

# 绘制数据
for i, (model, value) in enumerate(zip(models, values)):
    values_plot = np.concatenate((value, [value[0]]))
    ax.plot(angles, values_plot, 'o-', linewidth=2.5, label=model, color=colors[i])
    ax.fill(angles, values_plot, alpha=0.2, color=colors[i])

# 设置角度刻度标签（x轴）
ax.set_xticks(angles[:-1])
ax.set_xticklabels(metrics, fontsize=12, fontproperties=font)

# 添加图例
plt.legend(loc='upper right', bbox_to_anchor=(1.3, 1.1), prop=font)

# 添加标题
plt.title('ROUGE评分对比', y=1.08, fontsize=16, fontproperties=font)

# 保存图表
plt.savefig('./rouge_radar.png', dpi=300, bbox_inches='tight', facecolor='white')
plt.close()