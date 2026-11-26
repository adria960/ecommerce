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