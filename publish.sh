set -o allexport

echo "Publishing..."

./gradlew :bundleargs-annotation:publishToMavenLocal
./gradlew :bundleargs-processor:publishToMavenLocal
