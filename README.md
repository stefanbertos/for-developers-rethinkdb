# RethinkDb
## Docker
To run one Rethinkdb instance via docker use
`docker pull rethinkdb`
and 
`docker run --name rethinkdb -p 8080:8080 -p 28015:28015 -p 29015:29015 --volume=/data --workdir=/data --restart=no --runtime=runc -d rethinkdb`
## Docker compose
To run 2 Rethinkdb instances via docker compose use
`docker componse`
## Kubernetes
To run 3 Rethinkdb replicas in Kubernetes locally
1. first install minikube via https://minikube.sigs.k8s.io/docs/start/
2. helm via https://github.com/helm/helm/releases
3. install helm chart via https://artifacthub.io/packages/helm/pozetron/rethinkdb?modal=install
4. expose locally cluster ip service via git bash 
`export POD_NAME=$(kubectl get pods --namespace default -l "app=rethinkdb-proxy" -o jsonpath="{.items[0].metadata.name}")`
`kubectl port-forward $POD_NAME 28015:28015`
5. get database password via and set it in the application.yml
`echo Password: $(kubectl get secret --namespace default my-rethinkdb-rethinkdb -o jsonpath="{.data.rethinkdb-password}" | base64 --decode)`
6. open RethinkDB Admin Console via
`kubectl proxy`
7. and navigate to URL: http://localhost:8001/api/v1/namespaces/default/services/my-rethinkdb-rethinkdb-admin/proxy
   
# Local Run
1. start application via gradlew bootRun or Eclipse task
2. go to http://localhost:8080/webjars/swagger-ui/index.html
