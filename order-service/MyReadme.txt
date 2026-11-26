req post orders
{
    "customerId": "C001",
    "items": [
        {
            "skuCode": "LAPTOP-001",
            "quantity": 2,
            "price": 999.99
        },
        {
            "skuCode": "MOUSE-001",
            "quantity": 1,
            "price": 29.99
        }
    ]
}
RESP
{
    "id": 1,
    "orderNumber": "03f3c466-0b3e-4f78-93a8-ee6022a724b5",
    "customerId": "C001",
    "status": "PENDING",
    "totalAmount": 2029.97,
    "createdAt": "2026-11-26T01:37:43.793209900Z",
    "items": [
        {
            "id": 1,
            "skuCode": "LAPTOP-001",
            "quantity": 2,
            "price": 999.99
        },
        {
            "id": 2,
            "skuCode": "MOUSE-001",
            "quantity": 1,
            "price": 29.99
        }
    ]
}
------------------- After RestClient impl ---

POST http://localhost:8082/api/orders
{
    "customerId": "C001",
    "items": [
        {
            "skuCode": "LAPTOP-001",
            "quantity": 2
        }
    ]
}
RESP
{
    "id": 2,
    "orderNumber": "d5782e05-7b58-444c-baeb-5d578d51dd21",
    "customerId": "C001",
    "status": "PENDING",
    "totalAmount": 1999.98,
    "createdAt": "2026-11-26T19:37:17.827611900Z",
    "items": [
        {
            "id": 3,
            "skuCode": "LAPTOP-001",
            "quantity": 2,
            "price": 999.99
        }
    ]
}
--
11. What happens when a remote service fails?
    ----------------------------------------------------------
    Scenario	                    Current behaviour
    ----------------------------------------------------------
    Product Service unavailable	    RestClient throws exception
    Inventory Service unavailable	RestClient throws exception
    Insufficient stock              InsufficientStockException
    Remote service timeout	        RestClient throws exception
    Database persistence fails	    Local transaction rolls back

    In all these cases, the order is not successfully created.

    However, there is still an important limitation: checking availability does not reserve stock.
    The order remains PENDING, and Inventory Service quantities are unchanged.

    We will address this later.
 --------------------------------------------------------------------------------------------------


 12. Verify with Postman
     Start all three microservices.

         ----------------------------------------------------
          Scenario A — All services operational
          ----------------------------------------------------
             Create an order normally.

             Expected result: order successfully created. OK

          ----------------------------------------------------
          Scenario B — Product Service unavailable
          -----------------------------------------------------
             Stop Product Service and send another order request.

             Expected behaviour:
                 1. RestClient fails to connect.
                 2. Resilience4J retries the operation.
                 3. Circuit Breaker records failures.
                 4. Fallback throws ServiceUnavailableException.
                 5. No order is persisted.

---
Scenario A - OK
Scenario B
        **Postman response**
        {\
        &#x20;   "timestamp": "2026-11-26T22:28:15.971+00:00",\
        &#x20;   "status": 500,\
        &#x20;   "error": "Internal Server Error",\
        &#x20;   "path": "/api/orders"\
        }
        Console log
        --------------
        2026-11-26T23:28:15.941+01:00 ERROR 5648 --- [order-service] [nio-8082-exec-4] o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
        Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception
        [Request processing failed: com.ecommerce.exception.ServiceUnavailableException:

Product Service is temporarily unavailable] with root cause

java.net.ConnectException: Connection refused: getsockopt
	at java.base/sun.nio.ch.Net.pollConnect(Native Method) ~[na:na]
	at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:690) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(NioSocketImpl.jav

---
         ------------------------------------------------------------------
          Scenario C — Circuit opens
          ------------------------------------------------------------------
             Send additional order requests while Product Service is stopped.

             Once the failure threshold is reached, the circuit opens.

             Subsequent requests fail immediately without attempting a remote connection.


 Scenario D — Product Service recovery
             Restart Product Service.

             After the configured 10-second waiting period, the circuit transitions to HALF-OPEN and permits three trial calls.

             If recovery is successful, the circuit returns to CLOSED.
---
Postman - send req OK

logs
...
2026-11-26T23:45:48.739+01:00 ERROR 5648 --- [order-service] [nio-8082-exec-8] o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception
[Request processing failed: com.ecommerce.exception.ServiceUnavailableException:
Product Service is temporarily unavailable] with root cause

io.github.resilience4j.circuitbreaker.CallNotPermittedException:
CircuitBreaker 'productService' is OPEN and does not permit further calls
	at io.github.resilience4j.circuitbreaker.CallNotPermittedException.createCallNotPermittedException(CallNotPermittedException.java:48) ~[resilience4j-circuitbreaker-2.2.0.jar:2.2.0]
	at io.github
	...
Then:
Hibernate: select o1_0.id,o1_0.created_at,o1_0.customer_id,o1_0.order_number,o1_0.status,o1_0.total_amount from orders o1_0
Hibernate: select i1_0.order_id,i1_0.id,i1_0.price,i1_0.quantity,i1_0.sku_code from order_items i1_0 where i1_0.order_id=?
Hibernate: select i1_0.order_id,i1_0.id,i1_0.price,i1_0.quantity,i1_0.sku_code from order_items i1_0 where i1_0.order_id=?
Hibernate: select i1_0.order_id,i1_0.id,i1_0.price,i1_0.quantity,i1_0.sku_code from order_items i1_0 where i1_0.order_id=?
Hibernate: insert into orders (created_at,customer_id,order_number,status,total_amount) values (?,?,?,?,?)
Hibernate: insert into order_items (order_id,price,quantity,sku_code) values (?,?,?,?)
Hibernate: insert into orders (created_at,customer_id,order_number,status,total_amount) values (?,?,?,?,?)
Hibernate: insert into order_items (order_id,price,quantity,sku_code) values (?,?,?,?)
Hibernate: insert into orders (created_at,customer_id,order_number,status,total_amount) values (?,?,?,?,?)
Hibernate: insert into order_items (order_id,price,quantity,sku_code) values (?,?,?,?)
Hibernate: insert into orders (created_at,customer_id,order_number,status,total_amount) values (?,?,?,?,?)
Hibernate: insert into order_items (order_id,price,quantity,sku_code) values (?,?,?,?)
Hibernate: insert into orders (created_at,customer_id,order_number,status,total_amount) values (?,?,?,?,?)
Hibernate: insert into order_items (order_id,price,quantity,sku_code) values (?,?,?,?)

---

 13. Monitor circuit breakers with Actuator
        Add:
        management.endpoints.web.exposure.include=health,info,metrics,circuitbreakers,circuitbreakerevents,retries,retryevents

     Useful endpoints:
         Endpoint	                    Purpose
         ---------------------------------------------------------
         /actuator/circuitbreakers	    Registered circuit breakers
         /actuator/circuitbreakerevents	Circuit breaker events
         /actuator/retries	            Registered retry instances
         /actuator/retryevents	        Retry events
         /actuator/metrics	            Available metrics

 For example:
    GET http://localhost:8082/actuator/circuitbreakerevents

 These endpoints are particularly useful for observing state transitions.

 Expose operational endpoints only to authorized users in production.

