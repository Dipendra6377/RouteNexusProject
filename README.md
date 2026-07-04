1. Build Checkout Service Image

Go inside checkout-service project.

mvn clean package

Creates

target/checkout-service.jar

Build Docker image

docker build -t checkout-service .

Creates image

checkout-service
2. Build Traffic Router Image

Go inside router project.

mvn clean package

Build image

docker build -t traffic-router .

Creates

traffic-router
3. Create Docker Network
docker network create traffic-network

Purpose

All containers communicate using container names instead of localhost.

Example

router1
      |
checkout-v1
checkout-v2
checkout-v3
postgres
redis
nginx
4. Start PostgreSQL
docker run -d \
--name traffic-router-postgres \
--network traffic-network \
-e POSTGRES_DB=traffic_router \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-p 5432:5432 \
postgres:17

Purpose

Starts PostgreSQL database.

5. Start Redis
docker run -d \
--name traffic-router-redis \
--network traffic-network \
-p 6379:6379 \
redis:8

Purpose

Used for

Distributed Cache
Pub/Sub
Distributed Rate Limiter
6. Start Checkout Service v1
docker run -d \
--name checkout-v1 \
--network traffic-network \
-p 8081:8081 \
-e SERVER_PORT=8081 \
-e VERSION=v1 \
checkout-service
7. Start Checkout Service v2
docker run -d \
--name checkout-v2 \
--network traffic-network \
-p 8082:8082 \
-e SERVER_PORT=8082 \
-e VERSION=v2 \
checkout-service
8. Start Checkout Service v3
docker run -d \
--name checkout-v3 \
--network traffic-network \
-p 8083:8083 \
-e SERVER_PORT=8083 \
-e VERSION=v3 \
checkout-service

Purpose

Starts three backend instances.

9. Start Router 1
docker run -d \
--name router1 \
--network traffic-network \
-p 8080:8080 \
traffic-router

Purpose

First router node.

10. Start Router 2
docker run -d \
--name router2 \
--network traffic-network \
-p 8085:8080 \
traffic-router

Purpose

Second router node.

Notice

Inside container it still runs on

8080

Host exposes

8085
11. Start Prometheus
docker run -d \
--name prometheus \
--network traffic-network \
-p 9090:9090 \
-v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
prom/prometheus

Purpose

Scrapes metrics from both routers.

Open

http://localhost:9090
12. Start Grafana
docker run -d \
--name grafana \
--network traffic-network \
-p 3000:3000 \
grafana/grafana

Purpose

Visualize metrics.

Open

http://localhost:3000

Default

admin
admin
13. Start NGINX
docker run -d \
--name nginx \
--network traffic-network \
-p 80:80 \
-v "$(pwd)/docker/nginx.conf:/etc/nginx/nginx.conf:ro" \
nginx

Purpose

Acts as external load balancer.

Routes

Client
   ↓
NGINX
 ↓     ↓
Router1 Router2
14. Call Application

Without nginx

Router1

curl http://localhost:8080/checkout

Router2

curl http://localhost:8085/checkout

With nginx

curl http://localhost/checkout
15. View Running Containers
docker ps

Shows

Container ID
Ports
Status
16. View Logs

Router1

docker logs router1

Router2

docker logs router2

Live logs

docker logs -f router1
17. Enter Container
docker exec -it router1 sh

Purpose

Run commands inside container.

18. Check Networks
docker network ls

Shows all Docker networks.

19. Inspect Network
docker network inspect traffic-network

Purpose

Shows

Container IPs
Connected containers
DNS names
20. Inspect Container
docker inspect router1

Purpose

Shows

Environment variables
Network
Mounts
IP
Ports
21. Stop Container
docker stop router1

Purpose

Stops container without deleting it.

22. Start Again
docker start router1
23. Restart
docker restart router1
24. Remove Container
docker rm router1

If running

docker rm -f router1
25. Remove Image
docker rmi traffic-router

Deletes Docker image.

26. List Images
docker images

Shows all local images.

27. Rebuild After Code Changes

Whenever code changes

mvn clean package

Then

docker build -t traffic-router .

Remove old router

docker rm -f router1 router2

Start again.

28. Remove Everything

Containers

docker rm -f $(docker ps -aq)

Images

docker rmi -f $(docker images -q)

Volumes

docker volume prune

Networks

docker network prune
29. Check Health Endpoint

Router

curl http://localhost:8080/actuator/health

Checkout

curl http://localhost:8081/actuator/health
30. Check Metrics
curl http://localhost:8080/actuator/prometheus

Useful metrics include:

router_requests_total
router_retry_total
router_failover_total
router_cache_hit_total
router_cache_miss_total
router_circuit_open_total
router_circuit_close_total
rate_limit_allowed_total
rate_limit_rejected_total
rate_limit_remaining_tokens
31. Complete Architecture
                Client
                   │
             curl localhost
                   │
              ┌──────────┐
              │  NGINX   │
              └────┬─────┘
                   │
        ┌──────────┴──────────┐
        │                     │
    Router-1              Router-2
        │                     │
        ├──── Redis Cache ────┤
        ├──── Redis Pub/Sub ──┤
        ├── Distributed Rate Limiter
        │
     PostgreSQL
        │
  Service Discovery
        │
 ┌──────┼────────┐
 │      │        │
v1     v2       v3
