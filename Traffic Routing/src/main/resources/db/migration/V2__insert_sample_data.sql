INSERT INTO service_instance
(service_name,
 version,
 url,
 weight,
 active)
VALUES

    ('checkout-service',
     'v1',
     'http://localhost:8081',
     80,
     true),

    ('checkout-service',
     'v2',
     'http://localhost:8082',
     20,
     true),

    ('checkout-service',
     'v3',
     'http://localhost:8083',
     0,
     false);