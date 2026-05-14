import java.util.Comparator;

public class CriterioDeBuscaPorDescricao
        implements Comparator<ItemDePedido> {

    @Override
    public int compare(ItemDePedido item1, ItemDePedido item2) {

        return item1.getProduto().descricao
                .compareToIgnoreCase(
                        item2.getProduto().descricao
                );
    }
}