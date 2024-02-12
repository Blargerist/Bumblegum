# Bumblegum

A mod for fixing issues with bees. Namely, this mod stops bees from flying off into the void in skyblocks, getting stuck and filling up the mob cap.

 

Current fixes:

* Bee wander goal replaced with a modified version which stops bees from traveling too far from solid blocks in skyblocks
    * Wander distance from hive reduced from 22 to 8
    * Pathing end points cannot be in empty columns. There must be a block either above or below
    * Bees try to fly up if below the lowest chunk section containing blocks
    * If bee cannot path forwards, tries to path backwards
