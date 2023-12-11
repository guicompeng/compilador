# compilador

## Trabalho compilador

Para executar:
```
java -jar AnalisadorSemantico.jar [file-path]
```

Exemplo:
```
java -jar AnalisadorSemantico.jar testes/1-corrigido-semantico.txt
```

Para compilar:
```
javac src/*.java
jar cvfm AnalisadorSemantico.jar MANIFEST.MF src/*.class 
```
