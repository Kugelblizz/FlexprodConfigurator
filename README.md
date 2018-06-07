# iVolunteerPrototype v4

### Marketplace-DB:
docker run --name marketplace-db --restart=unless-stopped -p 27017:27017 -d mongo:3.6.2

### Marketplace-Workflow-DB
docker run --name marketplace-workflow-db --restart=unless-stopped -e MYSQL_DATABASE=workflow -e MYSQL_USER=workflow -e MYSQL_PASSWORD=workflow -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:5.7.22

### start fabric and deploy business network (precondition: hlf1.1):
1. cd ~/iVolunteerPrototype/blockchain/network/ivolunteer-blockchain
2. ./init

### start rest server:
1. cd ~/iVolunteerPrototype/blockchain/rest\ server
2. docker-compose up -d


