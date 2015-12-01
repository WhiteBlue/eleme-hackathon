export APP_HOST=0.0.0.0;
export APP_PORT=8080;


export DB_HOST=localhost;
export DB_PORT=3306;
export DB_NAME=eleme;
export DB_USER=root;
export DB_PASS="";

export REDIS_HOST=localhost;
export REDIS_PORT=6379;

mvn clean
mvn compile
mvn package

java -jar -Xmn400M -Xms800M -Xmx2048M -XX:NewRatio=3 -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 target/hackathon_build.jar
