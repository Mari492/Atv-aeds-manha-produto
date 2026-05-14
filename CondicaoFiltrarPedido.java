import java.util.function.Predicate;

public class CondicaoFiltrarPedido
        implements Predicate<Pedido> {

    private ItemDePedido itemBusca;

    public CondicaoFiltrarPedido(ItemDePedido itemBusca) {

        this.itemBusca = itemBusca;
    }

    @Override
    public boolean test(Pedido pedido) {

        ItemDePedido encontrado =
                pedido.getItensDoPedido().buscarPor(
                        new CriterioDeBuscaPorDescricao(),
                        itemBusca
                );

        return encontrado != null;
    }
}