{{/*
Create a default fully qualified app name.
*/}}
{{- define "books-chart.fullname" -}}
{{- printf "%s" .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "books-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "books-chart.labels" -}}
helm.sh/chart: {{ include "books-chart.chart" . }}
{{ include "books-chart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "books-chart.selectorLabels" -}}
app.kubernetes.io/name: {{ include "books-chart.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}