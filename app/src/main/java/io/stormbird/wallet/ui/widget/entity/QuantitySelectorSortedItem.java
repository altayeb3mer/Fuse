package io.stormbird.wallet.ui.widget.entity;

import io.stormbird.wallet.entity.Token;
import io.stormbird.wallet.ui.widget.holder.QuantitySelectorHolder;

/**
 * Created by James on 28/02/2018.
 */

public class QuantitySelectorSortedItem extends SortedItem<Token>
{
    public QuantitySelectorSortedItem(Token value)
    {
        super(QuantitySelectorHolder.VIEW_TYPE, value, 0);
    }

    @Override
    public int compare(SortedItem other)
    {
        return weight - other.weight;
    }

    @Override
    public boolean areContentsTheSame(SortedItem newItem)
    {
        return newItem.viewType == viewType
                || (((TokenBalanceSortedItem) newItem).value.getTicketCount() == value.getTicketCount())
                && ((TokenBalanceSortedItem) newItem).value.getFullName().equals(value.getFullName());
    }

    @Override
    public boolean areItemsTheSame(SortedItem other)
    {
        return other.viewType == viewType;
    }
}
