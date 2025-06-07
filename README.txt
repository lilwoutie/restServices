# Food Supplier Service

This service represents a RESTful API for a food supplier, developed using Spring Boot. It allows you to manage food items with properties such as name, item description, price, and delivery status.

## Data Model

Each food item has the following properties:

- `id` (Long): Unique identifier for the item.
- `name` (String): Name of the food item.
- `description` (String): Description of the food item.
- `price` (Double): Price of the food item.
- `delivered` (Boolean): Delivery status of the item.

## API Endpoints

### Get all food items

```
GET /items
```

Returns a list of all food items.

### Get a single item by ID

```
GET /items/{id}
```

Returns the item with the specified ID.

### Add a new item

```
POST /items
```

**Request body:**
```json
{
  "name": "Pizza",
  "description": "Large pepperoni pizza",
  "price": 12.99,
  "delivered": false
}
```

### Update an existing item

```
PUT /items/{id}
```

**Request body:**
```json
{
  "name": "Pizza",
  "description": "Large vegetarian pizza",
  "price": 13.49,
  "delivered": true
}
```

### Delete an item

```
DELETE /items/{id}
```

Deletes the item with the specified ID.

## Example curl Commands

- Create item:

```
curl -X POST http://localhost:8081/items   -H "Content-Type: application/json"   -d '{"name":"Pizza","description":"Pepperoni","price":12.99,"delivered":false}'
```

- Get all items:

```
curl http://localhost:8081/items
```

- Update item:

```
curl -X PUT http://localhost:8081/items/1   -H "Content-Type: application/json"   -d '{"name":"Burger","description":"Beef burger","price":9.99,"delivered":true}'
```

- Delete item:

```
curl -X DELETE http://localhost:8081/items/1
```

## Project Status

✅ Basic CRUD functionality implemented  
✅ In-memory H2 database used for development and testing  
❌ 2PC not yet implemented

---


-----------------------------------------------------------

 REST Order Service – API Overview

This service provides basic CRUD operations for handling orders. It's designed for use as a supplier in a distributed food ordering system.
📦 Base URL

http://localhost:8082

📌 Endpoints Summary (Before Distributed 2PC Coordination)
### 🛒 Order Management (/orders)
Method	Endpoint	Description	Request Body
GET	/orders	Get all orders	–
GET	/orders/{id}	Get an order by ID	–
POST	/orders	Create a new order	{"customerName": "...", "delivered": false}
DELETE	/orders/{id}	Delete an order by ID	–
PUT	/orders/{id}	Update an order by ID	{"customerName": "...", "delivered": true}
🔄 2PC-like Transaction API (/transaction)

These endpoints simulate basic two-phase commit behavior (single-service only):
Method	Endpoint	Description	Request Body / Params
POST	/transaction/prepare	Stage an order for a transaction	?transactionId=... + order JSON body
POST	/transaction/commit	Commit a previously staged order	?transactionId=...
POST	/transaction/rollback	Rollback a staged transaction	?transactionId=...

⚠ Note: commit or rollback must reference the same transactionId used in the prepare.

example usage via curl
# Create order
curl -X POST http://localhost:8082/orders \
 -H "Content-Type: application/json" \
 -d '{"customerName":"Alice", "delivered":false}'

# Get all orders
curl http://localhost:8082/orders

# Update order (PUT)
curl -X PUT http://localhost:8082/orders/1 \
 -H "Content-Type: application/json" \
 -d '{"customerName":"Alice", "delivered":true}'

# Delete order
curl -X DELETE http://localhost:8082/orders/1

# Transaction prepare
curl -X POST "http://localhost:8082/transaction/prepare?transactionId=tx1" \
 -H "Content-Type: application/json" \
 -d '{"customerName":"Bob", "delivered":false}'

# Commit transaction
curl -X POST "http://localhost:8082/transaction/commit?transactionId=tx1"

# Rollback transaction
curl -X POST "http://localhost:8082/transaction/rollback?transactionId=tx1"

 Technologies Used

    Java 17

    Spring Boot

    H2 in-memory database

    RESTful API with Spring MVC

    Maven

✅ Project Status

CRUD for orders

Basic 2PC simulation (within one service)

Distributed 2PC (planned)

Logging/Durability for recovery (planned)

