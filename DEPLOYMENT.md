## 🚀 **Deployment Commands**

### ⚙️ **Preparation**

Before running any scripts, ensure they are executable:

```bash
chmod +x *.sh
```

---

### 🔹 **One-Command Deployment (Recommended)**

#### 🧩 Local Development

```bash
./deploy.sh
```

#### 🌐 Production (on `srv998476`)

```bash
sudo ./deploy.sh
```

---

### 🔹 **Manual Control**

#### 🧩 Local

```bash
./switch-env.sh local && ./build.sh && ./start.sh
```

#### 🌐 Production

```bash
sudo ./switch-env.sh production && sudo ./deploy-production.sh --setup-nginx --setup-ssl
```
---