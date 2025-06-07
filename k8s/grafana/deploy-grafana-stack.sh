echo "✅ Checking Minikube status..."
minikube status || minikube start

echo "✅ Adding Grafana Helm charts..."
helm repo add grafana https://grafana.github.io/helm-charts

echo "✅ Updating Helm repo..."
helm repo update

echo "✅ Installing Grafana..."
helm upgrade --install --namespace=grafana-stack grafana grafana/grafana --create-namespace

echo "✅ Installing Grafana Loki..."
helm upgrade --install --namespace=grafana-stack loki grafana/loki --values loki-values.yaml

echo "✅ Installing Grafana Alloy..."
helm upgrade --install --namespace=grafana-stack alloy grafana/alloy

##kubectl get secret --namespace monitoring grafana-stack -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
