# 🎣 CustomFishing

**CustomFishing** は、Minecraftサーバー向けの  
✅ カスタム魚  
✅ 専用釣り竿  
✅ 鮮度システム  
✅ 捌き（フィレ）  
✅ 売却システム  
✅ 強力なマクロ対策

を備えた **本格派釣りプラグイン** です。

マクロ・放置対策を前提に設計されており、  
経済プラグイン（Vault）との連携も可能です。

---

## ✨ 主な機能

### 🎣 カスタム釣り
- 専用釣り竿でのみカスタム魚が釣れる
- 魚ごとに出現確率・サイズ・鮮度を設定可能
- Vanilla魚は自動で置き換え

### 🐟 鮮度システム
- 鮮度はランダム生成
- 食事・効果時間・売却価格に影響
- 鮮度ランク表示付き

| 鮮度 | 状態 |
|---|---|
| 90〜100 | 最高 |
| 70〜89 | 良好 |
| 50〜69 | 普通 |
| 30〜49 | 低下 |
| 〜29 | 劣悪 |

---

### 🔪 捌き（フィレ）
- 魚を右クリックで捌ける
- サイズ・鮮度によって取得量が変化
- フィレ専用アイテム & 効果付き
- 魚ごとに potion 効果を追加可能

---

### 💰 売却システム
- GUIによる一括売却
- 魚・フィレ両対応
- 売却ログ保存対応

---

## 🚫 マクロ・AFK対策（重要）

### ✅ 釣り中にランダムで発動
- **操作確認GUI**
- **移動確認（5マス以上）**

### ✅ 特徴
- 2〜5分間隔でランダム発動
- OP/管理者に通知
- 失敗時は自動通知
- 通知者は失敗者に TP 可能

---

## 📜 コマンド一覧

### 一般プレイヤー

| コマンド | 説明 |
|---|---|
| `/customfish list` | 釣れる魚の一覧を表示 |
| `/customfish sell` | 持っている魚・フィレを売却（GUI） |
| `/filleting` | 魚の捌き設定を確認 / 管理者用ヘルプ表示 |

### 管理者

| コマンド | 説明 |
|---|---|
| `/customfish add <name> <prob> <material> <lore> <min> <max> <chance>` | 魚を追加 |
| `/customfish remove <番号>` | 魚を削除 |
| `/customfish reload` | 魚データをリロード |
| `/customfish rod create` | 手に持っている釣り竿を専用釣り竿に |
| `/customfish rod remove` | 専用釣り竿を解除 |
| `/customfish on / off` | カスタム釣りの有効/無効切替 |
| `/filleting set <魚番号> <身の名前> <最小数> <最大数> <満腹度> <隠し満腹度> <CMD> [最小鮮度] [最大鮮度]` | 魚の捌き設定 |
| `/filleting addpotion <魚番号> <効果名> <秒数> <レベル>` | フィレに potion 効果を追加 |
| `/filleting clearpotion <魚番号>` | フィレの potion 効果を削除 |
| `/filleting remove <魚番号>` | 捌き設定を削除 |
| `/filleting info <魚番号>` | 魚の捌き情報を確認 |

---

## 🔑 権限

| 権限 | 内容 |
|---|---|
| `customfishing.sell` | 売却GUI |
| `customfishing.list` | 魚一覧表示 |
| `customfishing.admin` | 管理者操作全般 / マクロ通知 |
| `customfishing.filleting.admin` | フィレ設定管理 |

---

## 🧰 必須 / 推奨プラグイン

- ✅ **Vault**（経済連携）
- ✅ 対応経済プラグイン（EssentialsX など）
- ✅ Paper / Spigot 1.20+

---

## ⚙️ 導入方法

1. `CustomFishing.jar` を `plugins/` に配置
2. サーバー起動
3. `config.yml` と `fishdata.yml` を編集
4. `/customfish reload`

---

## 🛠 開発者向け

- 完全イベント駆動設計
- PersistentDataContainer 使用
- 高拡張性構成
- 大規模サーバー対応

---

## 📄 ライセンス

MIT License  
自由に改変・使用可能です。

---

## 🚀 今後の予定

- 鮮度の時間経過劣化
- Discord Webhook通知
- マクロ履歴GUI
- 自動ペナルティ設定

---

## ❤️ クレジット

Developed by **Ora000**  
Special Thanks to Minecraft Community
