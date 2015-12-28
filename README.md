# compiladore1
1 problema de compiladores

DOCUMENTAÇÃO DO PROJETO
Este repositório contém um projeto java implementado na Netbeans IDE.

Autor: Douglas Almeida

Autora: Jadna Almeida

Execução do código fonte

Para executar o analisador léxico é necessario ter instalado em sua máquina no minimo o ambiente de execução JAVA. Caso queira fazer alguma alteração no código presente na pasta src deve-se instalar o ambiente de desenvolvimento JAVA para implementação e simulação. 

Arquivos presentes

input.txt - Este arquivo contém o código fonte a ser analisado pelo analisador léxico. Caso queira testar outros códigos deve-se inserir-los neste arquivo.

analisadorlexico.java - Este arquivo contem a classe java resposavel pela análise léxica do código fonte.

output.txt - Este arquivo contém o resultado da análise léxica. Ele é criado ao executar o analisador lexico.

* Analisador Léxico

A implementação do analisador léxico para a linguagem de programação JAVA foi definida de acordo com as expressões regulares definidas  abaixo.

Palavra Token	Expressão regular correspondente

Palavras reservadas	class, const, else, if, new, read, write, return, void, while, int, float, bool, string, char, true, false, main

Identificadores	Letra(Letra|Dígito|_)*

Número	(-)?Dígito+(.Dígito+)?

Letra	(a..z|A..Z)

Dígito	0..9

Símbolo	ASCII de 33 a 126

Cadeia Constante	"(Letra|Dígito|Símbolo (exceto 34))*"

Caractere Constante	'(Letra|Dígito)'

Operadores	. + - * / ++ -- == != > >= < <= && || =

Delimitadores	;,(){}[]

Comentários de Linha	/* Isto é um comentário de bloco */''

Comentários de Bloco	// Isto é um comentário de linha```


A execução do analisador léxico resulta no arquivo output.txt que contem em cada linha um token representando os padrões analisados. 
