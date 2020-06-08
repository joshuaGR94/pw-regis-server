FROM airhacks/glassfish
COPY ./target/projectwork.war ${DEPLOYMENT_DIR}
