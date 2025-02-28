package edu.camilacanete.ej09p04.helper

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import edu.camilacanete.ej09p04.R

class ItemTouchHelperCallback (private val onSwiped : (Int) -> Unit) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

    private var swipeBackground: ColorDrawable? = null

    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    // Cuando se hace un deslizamiento (swipe), ejecuta el callback
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.adapterPosition)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val swipedFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(0, swipedFlags)
    }

    // Personalizar el dibujo del item cuando se deslice
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {


        val itemView = viewHolder.itemView
        val icon = ContextCompat.getDrawable(itemView.context, android.R.drawable.ic_menu_delete)
        val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2

        // Obtén el color del recurso
        val backgroundColor = ContextCompat.getColor(itemView.context, R.color.BackgroundDeleteItem)

        // Crea o actualiza el ColorDrawable con el color personalizado
        if (swipeBackground == null || swipeBackground!!.color != backgroundColor) {
            swipeBackground = ColorDrawable(backgroundColor)
        }

        // Dibuja el fondo rojo solo al deslizar hacia la izquierda
        if (dX < 0) {
            swipeBackground?.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            swipeBackground?.draw(c)

            // Dibuja el ícono de eliminación
            icon?.setBounds(
                itemView.right - iconMargin - (icon.intrinsicWidth),
                itemView.top + iconMargin,
                itemView.right - iconMargin,
                itemView.bottom - iconMargin
            )
            icon?.draw(c)
        }

        // Llama a la implementación base
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        /*
        val itemView = viewHolder.itemView

        // Cambiar el color de fondo según si se está deslizando
        if (dX < 0) {
            swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        } else {
            swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

        swipeBackground.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

         */


        /*
        // Icono de eliminar
        if (isCurrentlyActive){
            val icon = ContextCompat.getDrawable(itemView.context,
                android.R.drawable.ic_menu_delete
            )
            icon?.setBounds(
                itemView.right - 150, // Posición del icono en el item
                itemView.top + 20,
                itemView.right - 50,
                itemView.bottom - 20
            )
            icon?.draw(c)
        }

         */
    }






}