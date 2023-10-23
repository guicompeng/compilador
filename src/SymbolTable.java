package src;

import java.util.*;

public class SymbolTable {

    private List<RowSymbolTable> symbolTable;
    private int countLevel;

    public SymbolTable() {
        symbolTable = new ArrayList<>();
        countLevel = 1;
    }

    public void insertRowSymbolTable(RowSymbolTable row) {
        symbolTable.add(row);
    }

    public int getCountLevel() {
        return countLevel;
    }

    public void blockInput() {
        countLevel++;
    }

    public void blockOutput() {
        if (countLevel > 1) {
            // percorrer todas as linhas da tabela
            for (int i = symbolTable.size() - 1; i >= 0; i--) {
                // Todas as variaveis locais ao bloco sao apagadas
                if (symbolTable.get(i).getLevel() == countLevel) {
                    symbolTable.remove(i);
                }
            }
            // na saida do bloco, deve-se atualizar o nível
            countLevel--;
        }
    }

    public RowSymbolTable findRow(String lexeme) {
        // auxLevel comeca com o nivel mais alto para ser buscado
        for (int auxLevel = countLevel; auxLevel >= 1; auxLevel--) {
            // percorre cada linha na tabela
            for (int i = symbolTable.size() - 1; i >= 0; i--) {
                RowSymbolTable row = symbolTable.get(i);
                /*
                 * Se o nivel da linha for igual ao auxLevel (nivel que comeca no mais alto e vai diminuindo)
                 * E lexema for o que estamos procurando
                 */
                if (row.getLevel() == auxLevel && row.getLexeme().equals(lexeme)) {
                    return row;
                }
            }
        }
        // Retornar null se o lexema nao for encontrado em nenhum nível
        return null;
    }

}