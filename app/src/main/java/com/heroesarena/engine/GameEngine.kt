package com.heroesarena.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.heroesarena.models.*
import kotlin.math.*

/**
 * Core game engine - handles all game logic, rendering, and state
 */
class GameEngine {

    // Game state
    var gameState: GameState = GameState.WAITING
    var gameTime: Float = 0f
    var isPaused: Boolean = false
    private var lastUpdateTime: Long = 0

    // Map dimensions
    val mapWidth = 4000f
    val mapHeight = 4000f

    // Camera
    val camera = Camera()

    // Entities
    val playerHero: GameEntity.HeroEntity? = null
    private val entities = mutableListOf<GameEntity>()
    private val projectiles = mutableListOf<Projectile>()
    private val effects = mutableListOf<Effect>()

    // Team data
    val blueTeam = TeamData(Team.BLUE)
    val redTeam = TeamData(Team.RED)

    // Map objects
    private val towers = mutableListOf<Tower>()
    private val minions = mutableListOf<GameEntity.MinionEntity>()
    private val jungleMonsters = mutableListOf<JungleMonster>()
    private val bases = mutableListOf<Base>()

    // Minion spawn timer
    private var minionSpawnTimer: Float = 0f
    private val minionSpawnInterval: Float = 30f // seconds
    private var minionWaveCount: Int = 0

    // Jungle respawn timer
    private var jungleRespawnTimer: Float = 0f
    private val jungleRespawnInterval: Float = 120f

    // Game result
    var winningTeam: Team? = null

    // Paints for rendering
    private val grassPaint = Paint().apply { color = Color.rgb(46, 125, 50) }
    private val pathPaint = Paint().apply { color = Color.rgb(121, 85, 72) }
    private val waterPaint = Paint().apply { color = Color.rgb(21, 101, 192) }
    private val wallPaint = Paint().apply { color = Color.rgb(55, 71, 79) }
    private val towerPaint = Paint().apply { color = Color.rgb(100, 100, 100) }
    private val blueBasePaint = Paint().apply { color = Color.rgb(21, 101, 192) }
    private val redBasePaint = Paint().apply { color = Color.rgb(198, 40, 40) }
    private val hudPaint = Paint().apply { color = Color.WHITE; textSize = 24f; isAntiAlias = true }

    /**
     * Initialize a new game
     */
    fun startGame(mode: GameMode, selectedHero: Hero) {
        gameState = GameState.PLAYING
        gameTime = 0f
        isPaused = false
        winningTeam = null
        minionWaveCount = 0
        entities.clear()
        projectiles.clear()
        effects.clear()
        minions.clear()
        towers.clear()
        jungleMonsters.clear()
        bases.clear()

        // Initialize map
        initMap()

        // Create player hero
        val player = GameEntity.HeroEntity(
            id = "player",
            hero = selectedHero,
            team = Team.BLUE,
            x = mapWidth * 0.15f,
            y = mapHeight * 0.5f,
            isPlayerControlled = true
        )
        entities.add(player)

        // Create ally AI heroes
        createTeamHeroes(Team.BLUE, player.x, player.y, mode.playersPerTeam - 1)

        // Create enemy AI heroes
        createTeamHeroes(Team.RED, mapWidth * 0.85f, mapHeight * 0.5f, mode.playersPerTeam)

        lastUpdateTime = System.currentTimeMillis()
    }

    private fun initMap() {
        // Blue base (bottom-left)
        bases.add(Base(Team.BLUE, mapWidth * 0.08f, mapHeight * 0.92f, 300f))
        // Red base (top-right)
        bases.add(Base(Team.RED, mapWidth * 0.92f, mapHeight * 0.08f, 300f))

        // Blue team towers
        towers.add(Tower("blue_t1_top", Team.BLUE, mapWidth * 0.2f, mapHeight * 0.2f, 80f))
        towers.add(Tower("blue_t1_mid", Team.BLUE, mapWidth * 0.25f, mapHeight * 0.5f, 80f))
        towers.add(Tower("blue_t1_bot", Team.BLUE, mapWidth * 0.2f, mapHeight * 0.8f, 80f))
        towers.add(Tower("blue_t2_top", Team.BLUE, mapWidth * 0.13f, mapHeight * 0.18f, 100f))
        towers.add(Tower("blue_t2_mid", Team.BLUE, mapWidth * 0.15f, mapHeight * 0.5f, 100f))
        towers.add(Tower("blue_t2_bot", Team.BLUE, mapWidth * 0.13f, mapHeight * 0.82f, 100f))

        // Red team towers
        towers.add(Tower("red_t1_top", Team.RED, mapWidth * 0.8f, mapHeight * 0.2f, 80f))
        towers.add(Tower("red_t1_mid", Team.RED, mapWidth * 0.75f, mapHeight * 0.5f, 80f))
        towers.add(Tower("red_t1_bot", Team.RED, mapWidth * 0.8f, mapHeight * 0.8f, 80f))
        towers.add(Tower("red_t2_top", Team.RED, mapWidth * 0.87f, mapHeight * 0.18f, 100f))
        towers.add(Tower("red_t2_mid", Team.RED, mapWidth * 0.85f, mapHeight * 0.5f, 100f))
        towers.add(Tower("red_t2_bot", Team.RED, mapWidth * 0.87f, mapHeight * 0.82f, 100f))

        // Jungle monsters
        spawnJungleMonsters()
    }

    private fun createTeamHeroes(team: Team, baseX: Float, baseY: Float, count: Int) {
        val availableHeroes = HeroRegistry.getAllHeroes().shuffled().take(count)
        availableHeroes.forEachIndexed { i, hero ->
            val offsetY = (i - count / 2) * 80f
            val entity = GameEntity.HeroEntity(
                id = "${team.name.lowercase()}_ai_$i",
                hero = hero,
                team = team,
                x = baseX + (if (team == Team.BLUE) 50f else -50f),
                y = baseY + offsetY,
                isPlayerControlled = false
            )
            entities.add(entity)
        }
    }

    private fun spawnJungleMonsters() {
        // River crabs
        jungleMonsters.add(JungleMonster("jc_1", "River Crab", mapWidth * 0.4f, mapHeight * 0.5f, 500f, 20, 50))
        jungleMonsters.add(JungleMonster("jc_2", "River Crab", mapWidth * 0.6f, mapHeight * 0.5f, 500f, 20, 50))

        // Buff monsters
        jungleMonsters.add(JungleMonster("buff_1", "Blue Golem", mapWidth * 0.3f, mapHeight * 0.65f, 1500f, 50, 200))
        jungleMonsters.add(JungleMonster("buff_2", "Red Lizard", mapWidth * 0.3f, mapHeight * 0.35f, 1500f, 50, 200))
        jungleMonsters.add(JungleMonster("buff_3", "Blue Golem", mapWidth * 0.7f, mapHeight * 0.35f, 1500f, 50, 200))
        jungleMonsters.add(JungleMonster("buff_4", "Red Lizard", mapWidth * 0.7f, mapHeight * 0.65f, 1500f, 50, 200))

        // Lord (boss)
        jungleMonsters.add(JungleMonster("lord", "Lord", mapWidth * 0.5f, mapHeight * 0.5f, 8000f, 150, 500, isBoss = true))
    }

    /**
     * Spawn minion wave
     */
    private fun spawnMinionWave() {
        minionWaveCount++
        // Blue team minions - 3 lanes
        for (lane in listOf(Lane.TOP, Lane.MID, Lane.BOTTOM)) {
            val (sx, sy) = getLaneSpawn(Team.BLUE, lane)
            for (i in 0 until 3) {
                minions.add(GameEntity.MinionEntity(
                    id = "blue_minion_${minionWaveCount}_${lane}_$i",
                    team = Team.BLUE, x = sx + i * 30f, y = sy,
                    lane = lane
                ))
            }
        }
        // Red team minions
        for (lane in listOf(Lane.TOP, Lane.MID, Lane.BOTTOM)) {
            val (sx, sy) = getLaneSpawn(Team.RED, lane)
            for (i in 0 until 3) {
                minions.add(GameEntity.MinionEntity(
                    id = "red_minion_${minionWaveCount}_${lane}_$i",
                    team = Team.RED, x = sx + i * 30f, y = sy,
                    lane = lane
                ))
            }
        }
    }

    private fun getLaneSpawn(team: Team, lane: Lane): Pair<Float, Float> {
        if (team == Team.BLUE) {
            return when (lane) {
                Lane.TOP -> mapWidth * 0.1f to mapHeight * 0.15f
                Lane.MID -> mapWidth * 0.15f to mapHeight * 0.5f
                Lane.BOTTOM -> mapWidth * 0.1f to mapHeight * 0.85f
                else -> mapWidth * 0.15f to mapHeight * 0.5f
            }
        } else {
            return when (lane) {
                Lane.TOP -> mapWidth * 0.9f to mapHeight * 0.15f
                Lane.MID -> mapWidth * 0.85f to mapHeight * 0.5f
                Lane.BOTTOM -> mapWidth * 0.9f to mapHeight * 0.85f
                else -> mapWidth * 0.85f to mapHeight * 0.5f
            }
        }
    }

    /**
     * Main update loop - called every frame
     */
    fun update(deltaTime: Float) {
        if (gameState != GameState.PLAYING || isPaused) return

        gameTime += deltaTime

        // Spawn minions
        minionSpawnTimer += deltaTime
        if (minionSpawnTimer >= minionSpawnInterval) {
            minionSpawnTimer = 0f
            spawnMinionWave()
        }

        // Update entities
        entities.forEach { entity ->
            when (entity) {
                is GameEntity.HeroEntity -> updateHero(entity, deltaTime)
                is GameEntity.MinionEntity -> updateMinion(entity, deltaTime)
            }
        }

        // Update projectiles
        projectiles.forEach { it.update(deltaTime) }
        projectiles.removeAll { it.isDead }

        // Update effects
        effects.forEach { it.update(deltaTime) }
        effects.removeAll { it.isFinished }

        // Check tower aggro
        updateTowerAttacks(deltaTime)

        // Check win condition
        checkWinCondition()
    }

    private fun updateHero(hero: GameEntity.HeroEntity, dt: Float) {
        // Regenerate HP/MP
        hero.currentHp = min(hero.maxHp, hero.currentHp + hero.hpRegen * dt)
        hero.currentMp = min(hero.maxMp, hero.currentMp + hero.mpRegen * dt)

        // Update skill cooldowns
        hero.skillCooldowns.forEach { (skillId, cd) ->
            if (cd > 0) hero.skillCooldowns[skillId] = max(0f, cd - dt)
        }

        // AI behavior
        if (!hero.isPlayerControlled) {
            updateAI(hero, dt)
        }

        // Check level up
        checkLevelUp(hero)
    }

    private fun updateMinion(minion: GameEntity.MinionEntity, dt: Float) {
        if (minion.isDead) return

        // Move along lane toward enemy base
        val targetPos = getLaneTarget(minion.team, minion.lane)
        val dx = targetPos.first - minion.x
        val dy = targetPos.second - minion.y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist > 10f) {
            val speed = 150f * dt
            minion.x += (dx / dist) * speed
            minion.y += (dy / dist) * speed
        }

        // Auto attack nearby enemies
        val attackRange = 120f
        val nearestEnemy = findNearestEnemy(minion.team, minion.x, minion.y, attackRange)
        if (nearestEnemy != null) {
            minion.attackTimer += dt
            if (minion.attackTimer >= 1.0f) {
                minion.attackTimer = 0f
                dealDamage(minion, nearestEnemy, 25f, DamageType.PHYSICAL)
            }
        }
    }

    private fun updateAI(hero: GameEntity.HeroEntity, dt: Float) {
        hero.aiTimer += dt

        // Simple AI: Move toward nearest enemy, attack when in range
        if (hero.aiTimer >= 0.5f) {
            hero.aiTimer = 0f

            val nearestEnemy = findNearestEnemy(hero.team, hero.x, hero.y, 800f)
            if (nearestEnemy != null) {
                val dx = nearestEnemy.x - hero.x
                val dy = nearestEnemy.y - hero.y
                val dist = sqrt(dx * dx + dy * dy)

                if (dist > hero.hero.stats.attackRange) {
                    // Move toward enemy
                    val speed = hero.hero.stats.moveSpeed * dt
                    hero.x += (dx / dist) * speed * 2
                    hero.y += (dy / dist) * speed * 2
                } else {
                    // Attack
                    hero.attackTimer += 0.5f
                    if (hero.attackTimer >= 1f / hero.hero.stats.getAttackSpeedAtLevel(hero.level)) {
                        hero.attackTimer = 0f
                        val damage = hero.hero.stats.getAttackAtLevel(hero.level)
                        dealDamage(hero, nearestEnemy, damage, DamageType.PHYSICAL)
                    }

                    // Use skills
                    if (hero.skillCooldowns.values.all { it <= 0 }) {
                        val skill = hero.hero.skills.randomOrNull()
                        if (skill != null && skill.type != SkillType.PASSIVE) {
                            useSkill(hero, skill)
                        }
                    }
                }
            }
        }
    }

    private fun updateTowerAttacks(dt: Float) {
        towers.forEach { tower ->
            if (tower.isDestroyed) return@forEach
            tower.attackTimer += dt
            if (tower.attackTimer >= 1.0f) {
                val target = findNearestEnemy(tower.team, tower.x, tower.y, tower.range)
                if (target != null) {
                    tower.attackTimer = 0f
                    dealDamageTower(tower, target)
                }
            }
        }
    }

    private fun checkWinCondition() {
        // Check if a base is destroyed
        bases.forEach { base ->
            if (base.currentHp <= 0) {
                winningTeam = base.team.opposite()
                gameState = GameState.ENDED
            }
        }
    }

    private fun checkLevelUp(hero: GameEntity.HeroEntity) {
        val expNeeded = hero.level * 200f
        if (hero.exp >= expNeeded && hero.level < 15) {
            hero.exp -= expNeeded
            hero.level++
            hero.maxHp = hero.hero.stats.getHpAtLevel(hero.level)
            hero.maxMp = hero.hero.stats.getMpAtLevel(hero.level)
            hero.currentHp = hero.maxHp
            hero.currentMp = hero.maxMp

            effects.add(Effect.levelUp(hero.x, hero.y))
        }
    }

    // === Combat System ===

    fun useSkill(caster: GameEntity.HeroEntity, skill: Skill): Boolean {
        val cd = caster.skillCooldowns[skill.id] ?: 0f
        if (cd > 0) return false

        val manaCost = skill.getManaCostAtLevel(caster.skillLevels[skill.id] ?: 1)
        if (caster.currentMp < manaCost) return false

        caster.currentMp -= manaCost
        caster.skillCooldowns[skill.id] = skill.getCooldownAtLevel(caster.skillLevels[skill.id] ?: 1)

        val skillLevel = caster.skillLevels[skill.id] ?: 1
        val damage = skill.calculateTotalDamage(
            skillLevel,
            caster.hero.stats.getAttackAtLevel(caster.level),
            caster.hero.stats.getMagicAtLevel(caster.level)
        )

        // Find targets in range
        val targets = findEnemiesInArea(caster.team, caster.x, caster.y, skill.range, skill.aoeRadius)
        targets.forEach { target ->
            dealDamage(caster, target, damage, skill.damageType)

            // Apply CC
            if (skill.ccType != null) {
                applyCC(target, skill.ccType, skill.ccDuration)
            }
        }

        // Visual effect
        effects.add(Effect.skillCast(caster.x, caster.y, skill.damageType))

        return true
    }

    private fun dealDamage(attacker: GameEntity, target: GameEntity, damage: Float, type: DamageType) {
        val finalDamage = when (type) {
            DamageType.PHYSICAL -> damage * HeroStats().calculateDamageReduction(target.physicalDefense)
            DamageType.MAGIC -> damage * HeroStats().calculateDamageReduction(target.magicDefense)
            DamageType.TRUE -> damage
        }

        target.currentHp -= finalDamage
        effects.add(Effect.damageNumber(target.x, target.y, finalDamage.toInt(), type))

        if (target.currentHp <= 0) {
            target.currentHp = 0f
            target.isDead = true
            onEntityKilled(attacker, target)
        }
    }

    private fun dealDamageTower(tower: Tower, target: GameEntity) {
        val damage = tower.attackDamage
        target.currentHp -= damage
        effects.add(Effect.damageNumber(target.x, target.y, damage.toInt(), DamageType.PHYSICAL))

        if (target.currentHp <= 0) {
            target.currentHp = 0f
            target.isDead = true
        }
    }

    private fun onEntityKilled(killer: GameEntity, victim: GameEntity) {
        when (victim) {
            is GameEntity.HeroEntity -> {
                killer.gold += 300
                killer.exp += 200f
                if (killer is GameEntity.HeroEntity) {
                    killer.kills++
                    val team = if (killer.team == Team.BLUE) blueTeam else redTeam
                    team.kills++
                }
                victim.deaths++
                // Respawn timer
                victim.respawnTimer = 5f + victim.level * 2f
            }
            is GameEntity.MinionEntity -> {
                killer.gold += 20
                killer.exp += 30f
            }
        }
    }

    private fun applyCC(target: GameEntity, ccType: CCType, duration: Float) {
        target.activeCC = ccType
        target.ccDuration = duration
    }

    // === Helper Functions ===

    private fun findNearestEnemy(team: Team, x: Float, y: Float, range: Float): GameEntity? {
        return entities.filter { it.team != team && !it.isDead }.minByOrNull {
            val dx = it.x - x
            val dy = it.y - y
            sqrt(dx * dx + dy * dy)
        }?.takeIf {
            val dx = it.x - x
            val dy = it.y - y
            sqrt(dx * dx + dy * dy) <= range
        }
    }

    private fun findEnemiesInArea(team: Team, x: Float, y: Float, range: Float, radius: Float): List<GameEntity> {
        return entities.filter { it.team != team && !it.isDead }.filter {
            val dx = it.x - x
            val dy = it.y - y
            val dist = sqrt(dx * dx + dy * dy)
            dist <= range + radius
        }
    }

    /**
     * Move player hero
     */
    fun movePlayer(dx: Float, dy: Float, dt: Float) {
        val player = entities.find { it is GameEntity.HeroEntity && it.isPlayerControlled } as? GameEntity.HeroEntity ?: return
        if (player.isDead || player.activeCC == CCType.STUN || player.activeCC == CCType.ROOT) return

        val speed = player.hero.stats.moveSpeed * dt
        val length = sqrt(dx * dx + dy * dy)
        if (length > 0) {
            player.x = (player.x + (dx / length) * speed).coerceIn(0f, mapWidth)
            player.y = (player.y + (dy / length) * speed).coerceIn(0f, mapHeight)
        }

        // Update camera to follow player
        camera.targetX = player.x
        camera.targetY = player.y
    }

    /**
     * Player auto-attack nearest enemy
     */
    fun playerAutoAttack(dt: Float) {
        val player = entities.find { it is GameEntity.HeroEntity && it.isPlayerControlled } as? GameEntity.HeroEntity ?: return
        if (player.isDead) return

        player.attackTimer += dt
        val attackSpeed = player.hero.stats.getAttackSpeedAtLevel(player.level)
        if (player.attackTimer >= 1f / attackSpeed) {
            player.attackTimer = 0f
            val target = findNearestEnemy(player.team, player.x, player.y, player.hero.stats.attackRange)
            if (target != null) {
                val damage = player.hero.stats.getAttackAtLevel(player.level)
                dealDamage(player, target, damage, DamageType.PHYSICAL)
            }
        }
    }

    /**
     * Player use skill
     */
    fun playerUseSkill(skillType: SkillType): Boolean {
        val player = entities.find { it is GameEntity.HeroEntity && it.isPlayerControlled } as? GameEntity.HeroEntity ?: return false
        val skill = player.hero.getSkillByType(skillType) ?: return false
        return useSkill(player, skill)
    }

    // === Rendering ===

    fun render(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        camera.update(screenWidth.toFloat(), screenHeight.toFloat(), mapWidth, mapHeight)

        canvas.save()
        canvas.translate(-camera.x, -camera.y)

        // Draw map background
        drawMap(canvas)

        // Draw bases
        drawBases(canvas)

        // Draw towers
        drawTowers(canvas)

        // Draw jungle monsters
        drawJungleMonsters(canvas)

        // Draw minions
        drawMinions(canvas)

        // Draw entities
        drawEntities(canvas)

        // Draw projectiles
        projectiles.forEach { it.render(canvas) }

        // Draw effects
        effects.forEach { it.render(canvas) }

        canvas.restore()

        // Draw HUD (screen space)
        drawHUD(canvas, screenWidth, screenHeight)
    }

    private fun drawMap(canvas: Canvas) {
        // Grass
        canvas.drawRect(0f, 0f, mapWidth, mapHeight, grassPaint)

        // Lanes (paths)
        pathPaint.strokeWidth = 60f
        pathPaint.style = Paint.Style.STROKE

        // Top lane
        canvas.drawLine(mapWidth * 0.08f, mapHeight * 0.92f, mapWidth * 0.08f, mapHeight * 0.08f, pathPaint)
        canvas.drawLine(mapWidth * 0.08f, mapHeight * 0.08f, mapWidth * 0.92f, mapHeight * 0.08f, pathPaint)

        // Mid lane (diagonal)
        canvas.drawLine(mapWidth * 0.08f, mapHeight * 0.92f, mapWidth * 0.92f, mapHeight * 0.08f, pathPaint)

        // Bot lane
        canvas.drawLine(mapWidth * 0.08f, mapHeight * 0.92f, mapWidth * 0.92f, mapHeight * 0.92f, pathPaint)
        canvas.drawLine(mapWidth * 0.92f, mapHeight * 0.92f, mapWidth * 0.92f, mapHeight * 0.08f, pathPaint)

        // River (horizontal through middle)
        waterPaint.alpha = 150
        canvas.drawRect(mapWidth * 0.25f, mapHeight * 0.47f, mapWidth * 0.75f, mapHeight * 0.53f, waterPaint)

        // Jungle walls/bushes
        wallPaint.alpha = 80
        val bushSize = 60f
        for (i in 0..5) {
            canvas.drawOval(
                RectF(
                    mapWidth * 0.2f + i * bushSize * 2,
                    mapHeight * 0.3f,
                    mapWidth * 0.2f + i * bushSize * 2 + bushSize,
                    mapHeight * 0.3f + bushSize
                ), wallPaint
            )
            canvas.drawOval(
                RectF(
                    mapWidth * 0.5f + i * bushSize * 2,
                    mapHeight * 0.7f,
                    mapWidth * 0.5f + i * bushSize * 2 + bushSize,
                    mapHeight * 0.7f + bushSize
                ), wallPaint
            )
        }
    }

    private fun drawBases(canvas: Canvas) {
        bases.forEach { base ->
            val paint = if (base.team == Team.BLUE) blueBasePaint else redBasePaint
            paint.alpha = 100
            canvas.drawCircle(base.x, base.y, base.radius, paint)
            paint.alpha = 255

            // HP bar
            val hpRatio = base.currentHp / base.maxHp
            val barWidth = base.radius * 2
            val barHeight = 8f
            canvas.drawRect(
                base.x - barWidth / 2, base.y + base.radius + 10,
                base.x + barWidth / 2, base.y + base.radius + 10 + barHeight,
                Paint().apply { color = Color.DKGRAY }
            )
            canvas.drawRect(
                base.x - barWidth / 2, base.y + base.radius + 10,
                base.x - barWidth / 2 + barWidth * hpRatio, base.y + base.radius + 10 + barHeight,
                Paint().apply { color = if (hpRatio > 0.5) Color.GREEN else Color.RED }
            )
        }
    }

    private fun drawTowers(canvas: Canvas) {
        towers.forEach { tower ->
            if (tower.isDestroyed) return@forEach

            val paint = Paint().apply {
                color = if (tower.team == Team.BLUE) Color.rgb(21, 101, 192) else Color.rgb(198, 40, 40)
            }

            // Tower range indicator
            paint.alpha = 20
            canvas.drawCircle(tower.x, tower.y, tower.range, paint)
            paint.alpha = 255

            // Tower body
            canvas.drawCircle(tower.x, tower.y, 25f, paint)
            canvas.drawCircle(tower.x, tower.y, 15f, Paint().apply { color = Color.GRAY })

            // HP bar
            val hpRatio = tower.currentHp / tower.maxHp
            canvas.drawRect(tower.x - 20, tower.y - 40, tower.x + 20, tower.y - 35, Paint().apply { color = Color.DKGRAY })
            canvas.drawRect(tower.x - 20, tower.y - 40, tower.x - 20 + 40 * hpRatio, tower.y - 35, Paint().apply { color = Color.GREEN })
        }
    }

    private fun drawJungleMonsters(canvas: Canvas) {
        jungleMonsters.filter { !it.isDead }.forEach { monster ->
            val paint = Paint().apply { color = if (monster.isBoss) Color.rgb(255, 215, 0) else Color.rgb(139, 69, 19) }
            val size = if (monster.isBoss) 30f else 18f
            canvas.drawCircle(monster.x, monster.y, size, paint)

            // HP bar
            val hpRatio = monster.currentHp / monster.maxHp
            canvas.drawRect(monster.x - 15, monster.y - size - 10, monster.x + 15, monster.y - size - 5,
                Paint().apply { color = Color.DKGRAY })
            canvas.drawRect(monster.x - 15, monster.y - size - 10, monster.x - 15 + 30 * hpRatio, monster.y - size - 5,
                Paint().apply { color = Color.GREEN })
        }
    }

    private fun drawMinions(canvas: Canvas) {
        minions.filter { !it.isDead }.forEach { minion ->
            val paint = Paint().apply {
                color = if (minion.team == Team.BLUE) Color.rgb(100, 181, 246) else Color.rgb(239, 154, 154)
            }
            canvas.drawCircle(minion.x, minion.y, 10f, paint)

            // Small HP bar
            val hpRatio = minion.currentHp / minion.maxHp
            canvas.drawRect(minion.x - 8, minion.y - 16, minion.x + 8, minion.y - 13,
                Paint().apply { color = Color.DKGRAY })
            canvas.drawRect(minion.x - 8, minion.y - 16, minion.x - 8 + 16 * hpRatio, minion.y - 13,
                Paint().apply { color = Color.GREEN })
        }
    }

    private fun drawEntities(canvas: Canvas) {
        entities.forEach { entity ->
            if (entity.isDead) return@forEach

            val paint = Paint().apply {
                color = when {
                    entity is GameEntity.HeroEntity && entity.isPlayerControlled -> Color.rgb(0, 200, 255)
                    entity.team == Team.BLUE -> Color.rgb(66, 165, 245)
                    else -> Color.rgb(239, 83, 80)
                }
            }

            val size = if (entity is GameEntity.HeroEntity) 20f else 12f

            // Entity circle
            canvas.drawCircle(entity.x, entity.y, size, paint)
            canvas.drawCircle(entity.x, entity.y, size - 4, Paint().apply {
                color = Color.WHITE; alpha = 60
            })

            // Hero name
            if (entity is GameEntity.HeroEntity) {
                hudPaint.textSize = 12f
                hudPaint.color = Color.WHITE
                canvas.drawText(entity.hero.name, entity.x - 30, entity.y - 28, hudPaint)
            }

            // HP bar
            val hpRatio = entity.currentHp / entity.maxHp
            val barWidth = size * 2.5f
            canvas.drawRect(entity.x - barWidth / 2, entity.y - size - 12, entity.x + barWidth / 2, entity.y - size - 6,
                Paint().apply { color = Color.DKGRAY })
            canvas.drawRect(entity.x - barWidth / 2, entity.y - size - 12,
                entity.x - barWidth / 2 + barWidth * hpRatio, entity.y - size - 6,
                Paint().apply { color = if (hpRatio > 0.5) Color.GREEN else Color.RED })

            // MP bar (for heroes)
            if (entity is GameEntity.HeroEntity) {
                val mpRatio = entity.currentMp / entity.maxMp
                canvas.drawRect(entity.x - barWidth / 2, entity.y - size - 5,
                    entity.x + barWidth / 2, entity.y - size - 1,
                    Paint().apply { color = Color.DKGRAY })
                canvas.drawRect(entity.x - barWidth / 2, entity.y - size - 5,
                    entity.x - barWidth / 2 + barWidth * mpRatio, entity.y - size - 1,
                    Paint().apply { color = Color.BLUE })
            }
        }
    }

    private fun drawHUD(canvas: Canvas, sw: Int, sh: Int) {
        val player = entities.find { it is GameEntity.HeroEntity && it.isPlayerControlled } as? GameEntity.HeroEntity ?: return

        // Top bar - team scores
        hudPaint.textAlign = Paint.Align.CENTER
        hudPaint.textSize = 28f
        hudPaint.color = Color.rgb(66, 165, 245)
        canvas.drawText("${blueTeam.kills}", sw / 2f - 60, 40f, hudPaint)
        hudPaint.color = Color.WHITE
        canvas.drawText("VS", sw / 2f, 40f, hudPaint)
        hudPaint.color = Color.rgb(239, 83, 80)
        canvas.drawText("${redTeam.kills}", sw / 2f + 60, 40f, hudPaint)

        // Timer
        hudPaint.color = Color.WHITE
        hudPaint.textSize = 20f
        val mins = (gameTime / 60).toInt()
        val secs = (gameTime % 60).toInt()
        canvas.drawText(String.format("%02d:%02d", mins, secs), sw / 2f, 65f, hudPaint)

        // Bottom HP/MP bars
        val barX = sw / 2f - 150
        val barY = sh - 80f

        // HP bar
        canvas.drawRect(barX, barY, barX + 300, barY + 16, Paint().apply { color = Color.DKGRAY })
        val hpRatio = if (player.maxHp > 0) player.currentHp / player.maxHp else 0f
        canvas.drawRect(barX, barY, barX + 300 * hpRatio, barY + 16, Paint().apply { color = Color.GREEN })
        hudPaint.textSize = 12f
        hudPaint.color = Color.WHITE
        canvas.drawText("${player.currentHp.toInt()}/${player.maxHp.toInt()}", barX + 150, barY + 12, hudPaint)

        // MP bar
        canvas.drawRect(barX, barY + 20, barX + 300, barY + 36, Paint().apply { color = Color.DKGRAY })
        val mpRatio = if (player.maxMp > 0) player.currentMp / player.maxMp else 0f
        canvas.drawRect(barX, barY + 20, barX + 300 * mpRatio, barY + 36, Paint().apply { color = Color.BLUE })
        canvas.drawText("${player.currentMp.toInt()}/${player.maxMp.toInt()}", barX + 150, barY + 32, hudPaint)

        // Level & Gold
        hudPaint.textAlign = Paint.Align.LEFT
        hudPaint.textSize = 16f
        canvas.drawText("Lv.${player.level}", barX, barY - 5, hudPaint)
        hudPaint.color = Color.rgb(255, 215, 0)
        canvas.drawText("Gold: ${player.gold}", barX + 80, barY - 5, hudPaint)

        // KDA
        hudPaint.color = Color.WHITE
        canvas.drawText("KDA: ${player.kills}/${player.deaths}/${player.assists}", barX + 200, barY - 5, hudPaint)

        // Skill buttons
        val skillY = sh - 70f
        val skills = listOf(SkillType.SKILL_1, SkillType.SKILL_2, SkillType.SKILL_3, SkillType.ULTIMATE)
        skills.forEachIndexed { i, type ->
            val sx = sw - 200f + i * 50f
            val skill = player.hero.getSkillByType(type)
            val onCooldown = player.skillCooldowns[skill?.id ?: ""] ?: 0f > 0

            val skillPaint = Paint().apply {
                color = when {
                    onCooldown -> Color.GRAY
                    type == SkillType.ULTIMATE -> Color.rgb(156, 39, 176)
                    else -> Color.rgb(33, 150, 243)
                }
            }
            canvas.drawCircle(sx, skillY, 22f, skillPaint)
            hudPaint.textAlign = Paint.Align.CENTER
            hudPaint.textSize = 10f
            hudPaint.color = Color.WHITE
            val label = when (type) {
                SkillType.SKILL_1 -> "S1"
                SkillType.SKILL_2 -> "S2"
                SkillType.SKILL_3 -> "S3"
                SkillType.ULTIMATE -> "ULT"
                else -> "?"
            }
            canvas.drawText(label, sx, skillY + 4, hudPaint)

            if (onCooldown) {
                val cd = player.skillCooldowns[skill?.id ?: ""] ?: 0f
                hudPaint.textSize = 8f
                canvas.drawText(String.format("%.1f", cd), sx, skillY + 14, hudPaint)
            }
        }

        // Minimap
        drawMinimap(canvas, sw, sh)

        hudPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawMinimap(canvas: Canvas, sw: Int, sh: Int) {
        val mmSize = 130f
        val mmX = 10f
        val mmY = sh - mmSize - 10f
        val scale = mmSize / max(mapWidth, mapHeight)

        // Background
        canvas.drawRect(mmX, mmY, mmX + mmSize, mmY + mmSize, Paint().apply { color = Color.argb(180, 0, 0, 0) })

        // Border
        canvas.drawRect(mmX, mmY, mmX + mmSize, mmY + mmSize, Paint().apply {
            color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 2f
        })

        // Entities on minimap
        entities.filter { !it.isDead }.forEach { entity ->
            val paint = Paint().apply {
                color = when {
                    entity is GameEntity.HeroEntity && entity.isPlayerControlled -> Color.CYAN
                    entity.team == Team.BLUE -> Color.BLUE
                    else -> Color.RED
                }
            }
            val size = if (entity is GameEntity.HeroEntity) 4f else 2f
            canvas.drawCircle(mmX + entity.x * scale, mmY + entity.y * scale, size, paint)
        }

        // Towers on minimap
        towers.filter { !it.isDestroyed }.forEach { tower ->
            val paint = Paint().apply {
                color = if (tower.team == Team.BLUE) Color.BLUE else Color.RED
            }
            canvas.drawRect(
                mmX + tower.x * scale - 3, mmY + tower.y * scale - 3,
                mmX + tower.x * scale + 3, mmY + tower.y * scale + 3, paint
            )
        }

        // Camera viewport
        canvas.drawRect(
            mmX + camera.x * scale, mmY + camera.y * scale,
            mmX + (camera.x + camera.viewWidth) * scale,
            mmY + (camera.y + camera.viewHeight) * scale,
            Paint().apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 1f; alpha = 100 }
        )
    }

    // === Touch handling ===

    var touchStartX: Float = 0f
    var touchStartY: Float = 0f
    var isJoystickTouch: Boolean = false
    private val joystickDeadzone = 10f

    fun handleTouchEvent(event: MotionEvent, screenWidth: Int, screenHeight: Int) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Left half = joystick, right half = skills
                if (event.x < screenWidth * 0.4f) {
                    isJoystickTouch = true
                    touchStartX = event.x
                    touchStartY = event.y
                } else {
                    isJoystickTouch = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isJoystickTouch) {
                    val dx = event.x - touchStartX
                    val dy = event.y - touchStartY
                    val dt = 1f / 60f
                    movePlayer(dx, dy, dt)
                }
            }
            MotionEvent.ACTION_UP -> {
                isJoystickTouch = false
            }
        }
    }
}

// === Supporting classes ===

class Camera {
    var x: Float = 0f
    var y: Float = 0f
    var targetX: Float = 0f
    var targetY: Float = 0f
    var viewWidth: Float = 0f
    var viewHeight: Float = 0f
    private val smoothing = 0.1f

    fun update(screenW: Float, screenH: Float, mapW: Float, mapH: Float) {
        viewWidth = screenW
        viewHeight = screenH

        x += (targetX - viewWidth / 2 - x) * smoothing
        y += (targetY - viewHeight / 2 - y) * smoothing

        x = x.coerceIn(0f, (mapW - viewWidth).coerceAtLeast(0f))
        y = y.coerceIn(0f, (mapH - viewHeight).coerceAtLeast(0f))
    }
}

sealed class GameEntity(
    var x: Float, var y: Float,
    var team: Team, var currentHp: Float, var maxHp: Float
) {
    var isDead: Boolean = false
    var gold: Int = 0
    var exp: Float = 0f
    var attackTimer: Float = 0f
    var activeCC: CCType? = null
    var ccDuration: Float = 0f
    var physicalDefense: Float = 0f
    var magicDefense: Float = 0f

    class HeroEntity(
        id: String, val hero: Hero, team: Team, x: Float, y: Float,
        val isPlayerControlled: Boolean = false
    ) : GameEntity(x, y, team, hero.stats.hpBase, hero.stats.hpBase) {
        var currentMp: Float = hero.stats.mpBase
        var maxMp: Float = hero.stats.mpBase
        var level: Int = 1
        var hpRegen: Float = hero.stats.hpRegenBase
        var mpRegen: Float = hero.stats.mpRegenBase
        var kills: Int = 0
        var deaths: Int = 0
        var assists: Int = 0
        var skillCooldowns: MutableMap<String, Float> = mutableMapOf()
        var skillLevels: MutableMap<String, Int> = hero.skills.associate { it.id to 1 }.toMutableMap()
        var respawnTimer: Float = 0f
        var aiTimer: Float = 0f

        init {
            physicalDefense = hero.stats.defenseBase
            magicDefense = hero.stats.magicDefenseBase
        }
    }

    class MinionEntity(
        id: String, team: Team, x: Float, y: Float,
        val lane: Lane
    ) : GameEntity(x, y, team, 400f, 400f) {
        var moveSpeed: Float = 150f
    }
}

class TeamData(val team: Team) {
    var kills: Int = 0
    var towersDestroyed: Int = 0
    var gold: Int = 0
}

class Tower(
    val id: String, val team: Team, val x: Float, val y: Float,
    val attackDamage: Float, val range: Float = 400f, val maxHp: Float = 3000f
) {
    var currentHp: Float = maxHp
    var isDestroyed: Boolean = false
    var attackTimer: Float = 0f
}

class Base(
    val team: Team, val x: Float, val y: Float, val radius: Float,
    val maxHp: Float = 5000f
) {
    var currentHp: Float = maxHp
}

class JungleMonster(
    val id: String, val name: String, val x: Float, val y: Float,
    val maxHp: Float, val attackDamage: Float, val goldReward: Int,
    val isBoss: Boolean = false
) {
    var currentHp: Float = maxHp
    var isDead: Boolean = false
}

class Projectile(
    var x: Float, var y: Float,
    private val targetX: Float, private val targetY: Float,
    private val speed: Float = 500f,
    private val damage: Float = 0f,
    val team: Team = Team.NEUTRAL
) {
    var isDead: Boolean = false

    fun update(dt: Float) {
        val dx = targetX - x
        val dy = targetY - y
        val dist = sqrt(dx * dx + dy * dy)
        if (dist < 10f) {
            isDead = true
            return
        }
        x += (dx / dist) * speed * dt
        y += (dy / dist) * speed * dt
    }

    fun render(canvas: Canvas) {
        val paint = Paint().apply {
            color = if (team == Team.BLUE) Color.CYAN else Color.RED
        }
        canvas.drawCircle(x, y, 4f, paint)
    }
}

class Effect(
    var x: Float, var y: Float,
    val type: EffectType,
    var duration: Float = 0.5f,
    var value: Int = 0,
    val damageType: DamageType = DamageType.PHYSICAL
) {
    var timer: Float = 0f
    val isFinished: Boolean get() = timer >= duration

    fun update(dt: Float) {
        timer += dt
        y -= 30f * dt // Float upward
    }

    fun render(canvas: Canvas) {
        if (isFinished) return
        val alpha = ((1f - timer / duration) * 255).toInt().coerceIn(0, 255)
        val paint = Paint().apply {
            color = when (type) {
                EffectType.DAMAGE_NUMBER -> when (damageType) {
                    DamageType.PHYSICAL -> Color.WHITE
                    DamageType.MAGIC -> Color.rgb(100, 181, 246)
                    DamageType.TRUE -> Color.rgb(255, 215, 0)
                }
                EffectType.HEAL_NUMBER -> Color.GREEN
                EffectType.LEVEL_UP -> Color.rgb(255, 215, 0)
                EffectType.SKILL_CAST -> Color.rgb(156, 39, 176)
            }
            this.alpha = alpha
            textSize = when (type) {
                EffectType.LEVEL_UP -> 24f
                else -> 16f
            }
            isAntiAlias = true
        }

        when (type) {
            EffectType.DAMAGE_NUMBER, EffectType.HEAL_NUMBER -> {
                val text = if (type == EffectType.HEAL_NUMBER) "+$value" else "-$value"
                canvas.drawText(text, x, y, paint)
            }
            EffectType.LEVEL_UP -> {
                canvas.drawText("LEVEL UP!", x - 40, y, paint)
            }
            EffectType.SKILL_CAST -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                val radius = timer / duration * 60f
                canvas.drawCircle(x, y, radius, paint)
            }
        }
    }

    companion object {
        fun damageNumber(x: Float, y: Float, value: Int, type: DamageType) =
            Effect(x, y - 20, EffectType.DAMAGE_NUMBER, 1.0f, value, type)

        fun healNumber(x: Float, y: Float, value: Int) =
            Effect(x, y - 20, EffectType.HEAL_NUMBER, 1.0f, value)

        fun levelUp(x: Float, y: Float) =
            Effect(x, y - 40, EffectType.LEVEL_UP, 2.0f)

        fun skillCast(x: Float, y: Float, type: DamageType) =
            Effect(x, y, EffectType.SKILL_CAST, 0.5f, 0, type)
    }
}

enum class EffectType {
    DAMAGE_NUMBER, HEAL_NUMBER, LEVEL_UP, SKILL_CAST
}
