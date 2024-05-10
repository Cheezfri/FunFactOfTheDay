//package com.example.funfactoftheday.onboarding
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.cardview.widget.CardView
//import androidx.recyclerview.widget.RecyclerView
//
//class OnboardingAdapter: RecyclerView.Adapter<OnboardingAdapter.CardViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
//        return CardViewHolder(CardView(LayoutInflater.from(parent.context), parent))
//    }
//
//    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
//        holder.bind(Card.DECK[position])
//    }
//
//    override fun getItemCount(): Int {
//        return Card.DECK.size
//    }
//}
//}
//    class CardViewHolder internal constructor(private val cardView: CardView) :
//        RecyclerView.ViewHolder(cardView.view) {
//        internal fun bind(card: Card) {
//            cardView.bind(card)
//        }
//}