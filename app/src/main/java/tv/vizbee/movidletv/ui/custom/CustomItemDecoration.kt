package tv.vizbee.movidletv.ui.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration : RecyclerView.ItemDecoration() {
    private val dividerPaint = Paint().apply {
        color = 0xFF000000.toInt() // Black color
        strokeWidth = 4f // Line thickness
    }

    private val cornerRadius = 24 // Adjust the corner radius as needed

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        val adapter = parent.adapter ?: return

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            // Skip invalid positions and the last item
            if (position == RecyclerView.NO_POSITION || position == adapter.itemCount - 1) {
                continue
            }

            // Draw the divider below the current item
            val dividerLeft = child.left.toFloat()
            val dividerRight = child.right.toFloat()
            val dividerTop = child.bottom.toFloat()
            val dividerBottom = dividerTop + dividerPaint.strokeWidth

            canvas.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, dividerPaint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return
        val position = parent.getChildAdapterPosition(view)

        if (position == RecyclerView.NO_POSITION) return

        // Add top margin for the first item and bottom margin for the last item
        if (position == 0) {
            outRect.top = cornerRadius
        }
        if (position == adapter.itemCount - 1) {
            outRect.bottom = cornerRadius
        }

        // Add bottom margin for the divider
        outRect.bottom += (dividerPaint.strokeWidth.toInt())
    }
}