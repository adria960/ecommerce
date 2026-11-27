12. REST API
Method	    Endpoint
---------------------------------------------------------------
POST       	/api/inventory
GET	        /api/inventory
GET	        /api/inventory/{skuCode}
PATCH	    /api/inventory/{skuCode}/quantity?quantity=50
GET	        /api/inventory/availability?skuCode=LAPTOP-001&quantity=2

Postman verification
----------------------------
POST http://localhost:8083/api/inventory
{
    "skuCode": "LAPTOP-001",
    "quantity": 100
}
resp
{
    "id": 1,
    "skuCode": "LAPTOP-001",
    "quantity": 100
}

GET
http://localhost:8083/api/inventory/availability?skuCode=LAPTOP-001&quantity=2
Expected response:
true
