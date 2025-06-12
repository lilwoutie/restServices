
# Supplier REST Service – 2PC Compatible API

This project implements a **Supplier Service** in a distributed food-ordering system. It supports the **Two-Phase Commit (2PC)** protocol, allowing a broker or coordinator to manage distributed transactions across multiple services.

---

## 🌐 Deployment Info

- **VM IP Address**: `52.178.15.136`
- **VM Username**: `supplier1`
- **Port**: `8081`
- **Base URL**: `http://52.178.15.136:8081`

---

## 🚀 Quick Access: API Templates

| Operation      | Template                                                                 |
|----------------|---------------------------------------------------------------------------|
| View products (available only) | `GET /products`                                          |
| View all products (including unavailable) | `GET /product/all`                        |
| Begin a transaction (optional helper) | `POST /transaction/begin`                    |
| Prepare an order | `POST /transaction/prepare/{txnId}` + JSON body                         |
| Commit an order | `POST /transaction/commit/{txnId}`                                      |
| Rollback an order | `POST /transaction/rollback/{txnId}`                                  |
| View staged transactions | `GET /transaction/staged`                                     |

---

## ✅ Live Examples (on your deployed Azure VM)

### 1. View Available Products

```bash
curl http://52.178.15.136:8081/products
```

### 2. View **All** Products (including unavailable)

```bash
curl http://52.178.15.136:8081/product/all
```

---

## 🔄 Two-Phase Commit (2PC) Operations

### 3. Prepare a Transaction (Stage an Order)

```bash
curl -X POST http://52.178.15.136:8081/transaction/prepare/txn123      -H "Content-Type: application/json"      -d '{
           "productId": 1,
           "quantity": 2
         }'
```

### 4. Commit a Transaction

```bash
curl -X POST http://52.178.15.136:8081/transaction/commit/txn123
```

### 5. Rollback a Transaction

```bash
curl -X POST http://52.178.15.136:8081/transaction/rollback/txn123
```

### 6. View All Staged Transactions (Debug Only)

```bash
curl http://52.178.15.136:8081/transaction/staged
```

---

## 📦 Optional: Begin a Transaction (non-essential)

```bash
curl -X POST http://52.178.15.136:8081/transaction/begin
```

---

## 🤖 Broker Coordination Tips

To integrate this service into a distributed system with a **2PC broker**, follow this general coordination protocol:

### 1. **Prepare Phase**

For each involved service (like this supplier):

- The broker sends a `POST /transaction/prepare/{txnId}` with order details.
- Wait for `200 OK` if the supplier accepts the staged order.
- If any service returns a `4xx`, **abort the entire transaction**.

### 2. **Commit Phase**

If **all participants** responded with `200 OK` to the prepare:

- The broker sends `POST /transaction/commit/{txnId}` to each participant.

### 3. **Rollback Phase**

If **any participant failed** during the prepare phase:

- The broker sends `POST /transaction/rollback/{txnId}` to **all participants who prepared**.

### 4. **Idempotency & Error Handling**

- All 2PC endpoints are **idempotent**:
  - Re-sending the same `commit` or `rollback` will not cause errors or duplicates.
- The broker may safely retry operations if it missed a response.
- Edge cases like reused `txnId` with different content return HTTP 409 (Conflict).

### 5. **Service Unavailability**

- If the supplier is down during `commit` or `rollback`, the broker should retry until successful.
- Optionally implement **logging** or persistent staging to recover transactions.

---

## 🧪 Testing Recommendations

You can test the flow as follows:

1. Check current product inventory.
2. Prepare a transaction using a unique ID.
3. Verify the staged update using `/transaction/staged`.
4. Commit or rollback the transaction.
5. Re-check the product inventory.

---

## 🧳 Maintained By

**Deployed on Azure VM**

- SSH: `ssh -i ~/.ssh/id_rsa.pem supplier1@52.178.15.136`
- App Port: `8081`
- REST base: `http://52.178.15.136:8081`

---

