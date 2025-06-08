
echo "✅ Uninstalling Grafana Alloy..."
helm uninstall alloy -n grafana-stack

echo "✅ Uninstalling Grafana Loki..."
helm uninstall loki -n grafana-stack

echo "✅ Uninstalling Grafana..."
helm uninstall grafana -n grafana-stack

echo "✅ Deleting namespace grafana-stack..."
kubectl delete namespace grafana-stack

