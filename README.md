# RethinkDb
## Docker
To run one Rethinkdb instance via docker use
`docker pull rethinkdb`
and 
`docker run --name rethinkdb -p 8080:8080 -p 28015:28015 -p 29015:29015 --volume=/data --workdir=/data --restart=no --runtime=runc -d rethinkdb`
## Docker compose
To run 2 Rethinkdb instances via docker compose use
`docker-compose up`
`docker-compose down`
# Local Run
1. start application via gradlew bootRun 
2. go to http://localhost:8888/swagger-ui/index.html
