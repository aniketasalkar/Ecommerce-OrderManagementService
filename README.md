# Ecommerce-OrderManagementService

Order Management Service for Ecommerce Application

## API Documentation

### Order Controller

#### Create Order
- **Endpoint:** `/api/orders/create_order`
- **Method:** `POST`
- **Description:** Creates a new order.
- **Request Body:**
  - `OrderRequestDto` (e.g., order details, user ID, etc.)
- **Response:**
  - `201 Created`: Returns the created order details with a payment link.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during order creation.

#### Get Orders by User
- **Endpoint:** `/api/orders/get_orders/{userId}`
- **Method:** `GET`
- **Description:** Retrieves all orders for a specific user.
- **Query Parameters:**
  - `status` (optional, default: "ALL"): Filter orders by status.
- **Response:**
  - `200 OK`: Returns the list of orders for the user.
  - `404 Not Found`: If the user ID is not found.
  - `500 Internal Server Error`: If there is an error during order retrieval.

#### Update Order
- **Endpoint:** `/api/orders/orders/{id}/update_order`
- **Method:** `POST`
- **Description:** Updates an order with the given ID.
- **Request Body:**
  - `UpdateOrderDto` (e.g., order status, payment details, etc.)
- **Response:**
  - `200 OK`: Returns the updated order details.
  - `400 Bad Request`: If the request is invalid.
  - `404 Not Found`: If the order ID is not found.
  - `500 Internal Server Error`: If there is an error during order update.

#### Update Order Status
- **Endpoint:** `/api/orders/orders/{id}/update_status`
- **Method:** `POST`
- **Description:** Updates the status of an order with the given ID.
- **Request Body:**
  - `UpdateOrderStatusDto` (e.g., new status, user ID, etc.)
- **Response:**
  - `200 OK`: Returns the updated order status.
  - `400 Bad Request`: If the request is invalid.
  - `404 Not Found`: If the order ID is not found.
  - `500 Internal Server Error`: If there is an error during status update.

#### Get Order Tracking
- **Endpoint:** `/api/orders/orders/tracking/{id}`
- **Method:** `GET`
- **Description:** Retrieves the tracking information for an order.
- **Response:**
  - `200 OK`: Returns the tracking details of the order.
  - `404 Not Found`: If the order ID is not found.
  - `500 Internal Server Error`: If there is an error during tracking retrieval.

#### Cancel Order
- **Endpoint:** `/api/orders/orders/cancel/{id}`
- **Method:** `DELETE`
- **Description:** Cancels an order with the given ID.
- **Response:**
  - `200 OK`: Returns the details of the canceled order.
  - `404 Not Found`: If the order ID is not found.
  - `500 Internal Server Error`: If there is an error during order cancellation.

### User Authentication Service Client

#### Validate Token
- **Endpoint:** `/api/auth/{email}/validateToken`
- **Method:** `POST`
- **Description:** Validates a token for a given email.
- **Request Body:**
  - `ValidateAndRefreshTokenRequestDto` (e.g., token details)
- **Response:**
  - `200 OK`: Returns true if the token is valid.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during token validation.

#### Validate Service Registry Token
- **Endpoint:** `/api/auth/service/validate_token/`
- **Method:** `POST`
- **Description:** Validates a service registry token.
- **Request Body:**
  - `ValidateServiceTokenRequestDto` (e.g., token details)
- **Response:**
  - `200 OK`: Returns true if the token is valid.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during token validation.

#### Fetch Service Registry Token
- **Endpoint:** `/api/auth/service/fetch_token/{serviceName}`
- **Method:** `GET`
- **Description:** Fetches the service registry token for a given service name.
- **Response:**
  - `200 OK`: Returns the service registry token.
  - `404 Not Found`: If the service name is not found.
  - `500 Internal Server Error`: If there is an error during token retrieval.

### Inventory Service Client

#### Reserve Inventory Item
- **Endpoint:** `/api/inventory/reservation/reserve`
- **Method:** `POST`
- **Description:** Reserves an inventory item.
- **Request Body:**
  - `InventoryReservationRequestDto` (e.g., item details, quantity, etc.)
- **Response:**
  - `200 OK`: Returns the reservation details.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during reservation.

#### Revoke Inventory Reservation
- **Endpoint:** `/api/inventory/reservation/revoke`
- **Method:** `POST`
- **Description:** Revokes an inventory reservation.
- **Request Body:**
  - `RevokeInventoryReservationDto` (e.g., reservation ID, order ID, etc.)
- **Response:**
  - `200 OK`: Returns the revocation details.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during revocation.

### User Management Service Client

#### Get User by ID
- **Endpoint:** `/api/users/{userId}`
- **Method:** `GET`
- **Description:** Retrieves a user by their ID.
- **Response:**
  - `200 OK`: Returns the user details.
  - `404 Not Found`: If the user ID is not found.
  - `500 Internal Server Error`: If there is an error during user retrieval.

### Payment Service Client

#### Initiate Payment
- **Endpoint:** `/api/payment/initiate_payment`
- **Method:** `POST`
- **Description:** Initiates a payment process.
- **Request Body:**
  - `InitiatePaymentRequestDto` (e.g., amount, currency, user details, etc.)
- **Response:**
  - `200 OK`: Returns the payment initiation details.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during payment initiation.
