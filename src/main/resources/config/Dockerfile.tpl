FROM {{image}}

{{#each adds}}
ADD {{this}}
{{/each}}

CMD ["sh", "-c", "while :; do sleep 1; done"]
