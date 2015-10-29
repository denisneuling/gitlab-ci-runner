FROM {{image}}

{{#each envs}}
ENV {{this.key}} {{this.value}}
{{/each}}

{{#each adds}}
ADD {{this}}
{{/each}}

CMD ["sh", "-c", "while :; do sleep 1; done"]
