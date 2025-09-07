echo "✅ Checking Minikube status..."
minikube status || minikube start

echo "✅ Adding Prometheus Helm charts..."
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

echo "✅ Adding Grafana Helm charts..."
helm repo add grafana https://grafana.github.io/helm-charts

echo "✅ Updating Helm repo..."
helm repo update

echo "✅ Installing Prometheus..."
helm upgrade --install --namespace=prometheus-space prometheus prometheus-community/kube-prometheus-stack --create-namespace

echo "✅ Installing Grafana..."
helm upgrade --install --namespace=grafana-stack grafana grafana/grafana --create-namespace

echo "✅ Installing Grafana Loki..."
helm upgrade --install --namespace=grafana-stack loki grafana/loki --values loki-values.yaml

echo "✅ Installing Grafana Alloy..."
helm upgrade --install --namespace=grafana-stack alloy grafana/alloy

##kubectl get secret --namespace grafana-stack grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo

##To access the Prometheus web UI, use port forwarding : kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090:80 -n prometheus-space

##To access Grafana web UI, use port forwarding : kubectl port-forward svc/grafana -n grafana-stack 3012:80
