package com.salkcoding.tunalands

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.hasConnectedComponent
import io.mockk.mockk

object IsConnectedComponentsTest {
    fun `GIVEN several connected components are existed`() {
        val map = arrayOf(
            arrayOf(1, 1, 0, 0, 1, 1, 0),
            arrayOf(0, 1, 1, 1, 1, 1, 0),
            arrayOf(0, 1, 1, 0, 1, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 1, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0, 0, 0)
        )
        val mockLands = mockk<Lands>()
        for (i in map.indices) {
            for (j in map.indices) {
                if (map[i][j] == 1)
                    mockLands.landList.add("$i:$j")
            }
        }

        //It has to return true
        assert(!mockLands.hasConnectedComponent())
    }
}