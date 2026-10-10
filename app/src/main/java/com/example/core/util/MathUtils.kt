package com.example.core.util

import com.example.domain.tracing.NormalizedPoint
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object MathUtils {

    fun distance(p1: NormalizedPoint, p2: NormalizedPoint): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Calculates the minimum perpendicular distance from a test point [p]
     * to the line segment between [a] and [b].
     */
    fun pointToSegmentDistance(
        p: NormalizedPoint,
        a: NormalizedPoint,
        b: NormalizedPoint
    ): Float {
        val segmentLengthSq = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)
        if (segmentLengthSq == 0f) return distance(p, a)

        // Projection of p onto segment ab: t = [(p - a) . (b - a)] / |b - a|^2
        val t = max(0f, min(1f, ((p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y)) / segmentLengthSq))
        val projX = a.x + t * (b.x - a.x)
        val projY = a.y + t * (b.y - a.y)
        val dx = p.x - projX
        val dy = p.y - projY
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Finds the closest point and distance to a polyline [pathPoints].
     * Returns: (minDistance, closestSegmentIndex, projectionT)
     */
    fun closestPointOnPolyline(
        p: NormalizedPoint,
        pathPoints: List<NormalizedPoint>
    ): PolylineProjection {
        if (pathPoints.isEmpty()) return PolylineProjection(Float.MAX_VALUE, 0, 0f)
        if (pathPoints.size == 1) return PolylineProjection(distance(p, pathPoints[0]), 0, 0f)

        var minDistance = Float.MAX_VALUE
        var bestSegment = 0
        var bestT = 0f

        for (i in 0 until pathPoints.size - 1) {
            val a = pathPoints[i]
            val b = pathPoints[i + 1]
            val segmentLengthSq = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)
            val t = if (segmentLengthSq == 0f) 0f else {
                max(0f, min(1f, ((p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y)) / segmentLengthSq))
            }
            val projX = a.x + t * (b.x - a.x)
            val projY = a.y + t * (b.y - a.y)
            val dx = p.x - projX
            val dy = p.y - projY
            val dist = sqrt(dx * dx + dy * dy)

            if (dist < minDistance) {
                minDistance = dist
                bestSegment = i
                bestT = t
            }
        }

        return PolylineProjection(minDistance, bestSegment, bestT)
    }

    /**
     * Generates dense, evenly spaced checkpoints along a polyline.
     */
    fun generateCheckpoints(
        points: List<NormalizedPoint>,
        stepSize: Float = 0.025f
    ): List<NormalizedPoint> {
        if (points.size < 2) return points
        val result = mutableListOf<NormalizedPoint>()
        result.add(points[0])

        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val segDist = distance(p1, p2)
            val steps = max(1, kotlin.math.ceil(segDist / stepSize).toInt())
            for (step in 1..steps) {
                val t = step / steps.toFloat()
                val x = p1.x + t * (p2.x - p1.x)
                val y = p1.y + t * (p2.y - p1.y)
                result.add(NormalizedPoint(x, y))
            }
        }
        return result
    }

    /**
     * Calculates the normalized direction tangent angle (in radians) at a given point on a polyline.
     */
    fun tangentAngle(p1: NormalizedPoint, p2: NormalizedPoint): Float {
        return kotlin.math.atan2(p2.y - p1.y, p2.x - p1.x)
    }

    data class PolylineProjection(
        val distance: Float,
        val segmentIndex: Int,
        val segmentT: Float
    )
}
