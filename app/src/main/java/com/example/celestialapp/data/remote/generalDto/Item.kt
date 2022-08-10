package com.example.celestialapp.data.remote.generalDto

data class Item(
    val `data`: List<Data>,
    val href: String,
    val links: List<Link>
)
{
    fun isNotEmpty(): Boolean {
        val title = this.data.first().title
        val image = this.links.first().href

        return !(title == null || image == null)
    }
}