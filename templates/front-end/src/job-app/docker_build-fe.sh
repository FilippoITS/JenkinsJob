# Nome e tag dell'immagine (corrispondente al deployment)
IMAGE_NAME="job-fe"
IMAGE_TAG="1.0"

# Attiva Docker interno di Minikube
eval $(minikube docker-env)

# Costruisci l'immagine
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

echo "Immagine ${IMAGE_NAME}:${IMAGE_TAG} creata con successo!"
