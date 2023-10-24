package me.wcy.music.discover.ranking.item

import me.wcy.music.databinding.ItemRankingTitleBinding
import me.wcy.music.discover.ranking.viewmodel.RankingViewModel
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/10/25.
 */
class RankingTitleItemBinding : RItemBinder<ItemRankingTitleBinding, RankingViewModel.TitleData>() {
    override fun onBind(
        viewBinding: ItemRankingTitleBinding,
        item: RankingViewModel.TitleData,
        position: Int
    ) {
        viewBinding.root.text = item.title
    }
}