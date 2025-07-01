# BB 账本 – 公共 API & 组件文档

本项目是一款使用 Kotlin 开发的 Android 记账应用，核心代码位于 `app/src/main/java`. 本文档覆盖了所有**公开的类、主要方法以及可复用组件**，并给出了使用示例。

> ⚠️ 说明  
> * **公开 API** 指 _在业务代码之外可能被其他模块/页面调用的类、方法或常量_。  
> * 私有 (`private`) 或仅在类内部使用的成员未列出。  
> * 文中的 *示例* 基于 `Kotlin`。

---

## 目录

1. [应用入口](#应用入口)
2. [路由常量 `Path`](#路由常量-path)
3. [全局数据对象](#全局数据对象)
4. [数据库访问](#数据库访问)
5. [UI 组件](#ui-组件)
6. [Activity – 页面](#activity-–-页面)
7. [Fragment – 模块化界面](#fragment-–-模块化界面)
8. [Adapter – 列表适配器](#adapter-–-列表适配器)
9. [数据模型](#数据模型)

---

## 应用入口

### `AccountApplication`
`cn.edu.sicnu.account.AccountApplication`

| 方法 | 说明 |
|------|------|
| `onCreate()` | 初始化 [ARouter](https://github.com/alibaba/ARouter)。调试版本开启日志及调试模式。 |

**使用示例**
```kotlin
class MyApp : AccountApplication() {
    override fun onCreate() {
        super.onCreate()
        // 这里可继续做其它全局初始化
    }
}
```

在 `AndroidManifest.xml` 的 `<application>` 节点里声明：
```xml
<application
    android:name=".AccountApplication"
    ... >
```

---

## 路由常量 `Path`
`cn.edu.sicnu.account.arouter.Path`

| 常量 | 路径 | 目标页面 |
|-------|-------|---------|
| `main` | `/account/main` | `MainActivity` |
| `login` | `/account/login` | `LoginActivity` |
| `register` | `/account/register` | `RegisterActivity` |
| `edit` | `/account/edit` | `EditActivity` |

**导航示例**
```kotlin
ARouter.getInstance().build(Path.login).navigation()
```

---

## 全局数据对象

### `Time`
> `cn.edu.sicnu.account.data.Time`

提供当前日期信息。

| 成员 | 类型 | 含义 |
|------|------|------|
| `year` | `Int` | 当前年份 |
| `month` | `Int` | 当前月份 (1-12) |
| `day` | `Int` | 当月日期 |

**示例**
```kotlin
val today = "${Time.year}-${Time.month}-${Time.day}"
```

### `User`
> `cn.edu.sicnu.account.auth.User`

| 成员 | 类型 | 说明 |
|------|------|------|
| `isLogin` | `Boolean` | 全局登录标记，在登录/游客模式间共享 |

---

## 数据库访问

### `SqliteOpenHelper`
> `cn.edu.sicnu.account.sqlite.SqliteOpenHelper`

封装本地 SQLite 数据库，提供两张核心表：`user` 与 `bills`。

| 表 | 字段 | 含义 |
|----|------|------|
| `user` | `_id INTEGER` (主键)<br>`phone TEXT`<br>`password TEXT` | 用户信息 |
| `bills` | `_id INTEGER` (主键)<br>`in_or_out INT` (1 收入 / 0 支出)<br>`money REAL`<br>`type TEXT` (分类)<br>`note TEXT`<br>`time TEXT` (格式 `yyyy.M.d`) | 记账数据 |

**初始化默认数据**
`onCreate()` 中插入了两条用户数据及三条账单数据，便于演示。

**快速写入示例**
```kotlin
val helper = SqliteOpenHelper(context)
val db = helper.writableDatabase
// 插入一条支出记录
val sql = "insert into bills (in_or_out, money, type, note, time) " +
          "values (0, 88.0, '交通', '地铁', '2025.7.1')"
db.execSQL(sql)
```

> 建议统一通过 [`EditActivity`](#editactivity) 或自己封装 DAO 层进行访问，而非直接拼接 SQL。

---

## UI 组件

### `ItemAdapter`
> `cn.edu.sicnu.account.adapter.ItemAdapter`

自定义 `BaseAdapter`，将 `Items` 数据渲染为 `ListView` 条目。

| 构造函数 | 说明 |
|-----------|------|
| `ItemAdapter(context: Context, arr: ArrayList<Items>)` | `arr` 传入要展示的数据集合 |

**绑定示例**
```kotlin
val adapter = ItemAdapter(this, items)
listView.adapter = adapter
```

---

## Activity – 页面

### 1. `MainActivity`
> `cn.edu.sicnu.account.MainActivity`

* 功能：
  * 底部导航栏在 4 个 `Fragment` 间切换。
  * 首次进入向系统发送常驻通知，快捷打开应用。
* 关键方法：
  * `initNotification()` – 构建并发送通知。
  * `initView()` – 创建并预加载所有 `Fragment` 实例。
  * `bindListener()` – 绑定导航与新增账单按钮事件。

**示例 – 打开新增账单页**
```kotlin
iv_add.setOnClickListener {
    ARouter.getInstance().build(Path.edit).navigation()
}
```

### 2. `EditActivity`
> `/account/edit`

用于新增一条记账记录。

| 公开交互 | 说明 |
|-----------|------|
| 单选 `rb_income / rb_outcome` | 选择收入/支出 |
| `et_money` | 输入金额 |
| `sp_type` | 选择分类 |
| `et_note` | 输入备注 |
| `tv_confirm` | 校验 & 写入数据库 |

### 3. `LoginActivity` & `RegisterActivity`

* `LoginActivity` – 手机号 & 密码登陆，或游客模式 (`tv_host`)。
* `RegisterActivity` – 手机验证码验证后注册账号。

> 路由地址分别为 `Path.login` 与 `Path.register`。

---

## Fragment – 模块化界面

| Fragment | 路由/父页面 | 主要职责 |
|-----------|------------|---------|
| `AccountDetailFragment` | `MainActivity` | 当前月份账单列表、收入/支出统计；支持点击条目**修改/删除** |
| `ChartFragment` | `MainActivity` | 使用 MPAndroidChart 展示收入/支出饼状图及每日柱状图 |
| `DiscoverFragment` | `MainActivity` | 预留功能 – 发现页 |
| `MineFragment` | `MainActivity` | 个人中心，待扩展 |

### 公共静态工厂
每个 Fragment 都实现了：
```kotlin
companion object {
    fun newInstance(): XxxFragment { ... }
}
```
以避免在外部直接调用构造函数。

---

## Adapter – 列表适配器

详见 [UI 组件 – `ItemAdapter`](#itemadapter)。

---

## 数据模型

### `Items`
`data class Items`

| 属性 | 类型 | 说明 |
|------|------|------|
| `id` | `Int?` | 账单主键，自动增长 |
| `in_or_out` | `Int?` | 1 收入 / 0 支出 |
| `money` | `Double?` | 金额 |
| `type` | `String?` | 分类标签 |
| `note` | `String?` | 备注 |
| `time` | `String?` | 日期，默认当前日期 |

---

## 常见调用流程

### 1. 应用启动
1. `AccountApplication` 初始化 ARouter。  
2. `LoginActivity` / `MainActivity` 根据 `User.isLogin` 及路由进入。

### 2. 记一笔账
1. 在 `MainActivity` 点击 **+** 按钮 (`iv_add`) → ARouter 跳转至 `EditActivity`。
2. 在 `EditActivity` 选择类型、填写金额和备注，点击确定后向 `SqliteOpenHelper` 写入一条账单记录。
3. Activity 结束后返回 `MainActivity`，`AccountDetailFragment` 会重新查询数据库并刷新。

### 3. 查看统计
1. 在底部导航选择 **统计**（`ChartFragment`）。
2. 默认展示当前年月的饼状图 + 柱状图。
3. 点击 **选择日期** 可自定义查询年月。

---

## 依赖 & 第三方库

| 库 | 用途 |
|----|------|
| [ARouter](https://github.com/alibaba/ARouter) | 模块间路由与参数传递 |
| [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) | 绘制统计图表 |
| [Android Support v4 / v7](https://developer.android.com/topic/libraries/support-library) | Fragment, AppCompat 支持包 |
| [Anko](https://github.com/Kotlin/anko) | DSL UI 构建 & 常用扩展函数 |

---

## 贡献指南

* 文档中的类/方法如有变动，请务必同步更新此文件。  
* 建议新增业务模块时：
  1. 先在 `Path` 注册路由常量；
  2. 对外暴露的逻辑编写 **KDoc** 注释；
  3. 在本文件相应章节添加说明与示例。

---

> 文档最后更新：2025-07-01