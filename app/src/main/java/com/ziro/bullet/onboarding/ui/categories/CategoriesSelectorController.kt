package com.ziro.bullet.onboarding.ui.categories

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.ziro.bullet.BulletApp
import com.ziro.bullet.R
import com.ziro.bullet.data.dataclass.ContentLanguage
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.dataclass.Region
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.onboarding.ui.categories.item_model.OnboardingHeaderItemModel
import com.ziro.bullet.onboarding.ui.categories.item_model.onboardingCategoriesItem
import com.ziro.bullet.onboarding.ui.categories.item_model.onboardingHeaderItem

class CategoriesSelectorController(private val itemInteractionListener: ItemInteractionListener) :
    EpoxyController() {

    var collections: OnBoardingModel? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var topicList = mutableListOf<Topics>()

    var regionList = mutableListOf<Region>()

    var languageList = mutableListOf<ContentLanguage>()

    val selectedMoreThanMinimum: Boolean
        get() = (topicList.size + regionList.size + languageList.size) >= 3

    val selectedCategories: SaveOnBoardingModel
        get() = SaveOnBoardingModel(
            languageList.map {
                it.id
            },
            regionList.map {
                it.id
            },
            topicList.map {
                it.id
            }
        )

    override fun buildModels() {

        onboardingHeaderItem {
            id(OnboardingHeaderItemModel::class.simpleName)
            headerTitle(BulletApp.getInstance().applicationContext.getString(R.string.onboarding_title))
            headerSubTitle(BulletApp.getInstance().applicationContext.getString(R.string.onboarding_subtitle))
            spanSizeOverride { _, _, _ -> 2 }
        }

        collections?.topics?.forEach {
            onboardingCategoriesItem {
                id("topics${it.id}")
                imageUrl(it.image?.ifEmpty { it.icon } ?: "")
                name(it.name)
                itemSelected(this@CategoriesSelectorController.topicList.any { item -> item.id == it.id })
                onItemClick {
                    val hasSelected =
                        this@CategoriesSelectorController.topicList.any { item -> item.id == it.id }
                    if (!hasSelected) {
                        this@CategoriesSelectorController.topicList.add(it)
                    } else this@CategoriesSelectorController.topicList.remove(it)
                    this@CategoriesSelectorController.requestModelBuild()
                    this@CategoriesSelectorController.itemInteractionListener.onItemSelection()
                }
            }
        }

        collections?.regions?.forEach {
            onboardingCategoriesItem {
                id("topics${it.id}")
                imageUrl(it.image.ifEmpty { it.flag } ?: "")
                name(it.name)
                itemSelected(this@CategoriesSelectorController.regionList.any { item -> item.id == it.id })
                onItemClick {
                    val hasSelected =
                        this@CategoriesSelectorController.regionList.any { item -> item.id == it.id }
                    if (!hasSelected) {
                        this@CategoriesSelectorController.regionList.add(it)
                    } else this@CategoriesSelectorController.regionList.remove(it)
                    this@CategoriesSelectorController.requestModelBuild()
                    this@CategoriesSelectorController.itemInteractionListener.onItemSelection()
                }
            }
        }

        collections?.languageList?.forEach {
            onboardingCategoriesItem {
                id("topics${it.id}")
                imageUrl(it.image)
                name(it.name)
                itemSelected(this@CategoriesSelectorController.languageList.any { item -> item.id == it.id })
                onItemClick {
                    val hasSelected =
                        this@CategoriesSelectorController.languageList.any { item -> item.id == it.id }
                    if (!hasSelected) {
                        this@CategoriesSelectorController.languageList.add(it)
                    } else this@CategoriesSelectorController.languageList.remove(it)
                    this@CategoriesSelectorController.requestModelBuild()
                    this@CategoriesSelectorController.itemInteractionListener.onItemSelection()
                }
            }
        }
    }

    interface ItemInteractionListener {
        fun onItemSelection()
    }

}