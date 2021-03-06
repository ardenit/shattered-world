package com.mirage.gameview.drawers

import com.mirage.gameview.drawers.templates.EmptyDrawerTemplate
import com.mirage.gameview.drawers.templates.HumanoidDrawerTemplate
import com.mirage.gameview.utils.loadBuildingDrawersFromTemplate
import com.mirage.gameview.utils.loadEntityDrawersFromTemplate
import com.mirage.utils.Log
import com.mirage.utils.datastructures.Rectangle
import com.mirage.utils.game.maps.SceneLoader
import com.mirage.utils.game.objects.properties.Equipment
import com.mirage.utils.game.objects.properties.MoveDirection
import com.mirage.utils.game.objects.simplified.SimplifiedBuilding
import com.mirage.utils.game.objects.simplified.SimplifiedEntity
import com.mirage.utils.game.states.SimplifiedState
import com.mirage.utils.game.states.StateDifference
import com.mirage.utils.virtualscreen.VirtualScreen

class DrawersManagerImpl(private val sceneLoader: SceneLoader) : DrawersManager {

    /**
     * Словарь, в котором кэшируются шаблонные представления.
     * Первый ключ - название шаблона, второй ключ - состояние объекта.
     */
    private val cachedBuildingDrawerTemplates: MutableMap<String, MutableMap<String, DrawerTemplate>> = HashMap()
    private val cachedEntityDrawerTemplates: MutableMap<String, MutableMap<String, DrawerTemplate>> = HashMap()

    /**
     * Словарь, в котором хранятся представления конкретных объектов в их текущем состоянии.
     * Ключ - ID объекта.
     */
    private val buildingDrawers: MutableMap<Long, Drawer> = HashMap()
    private val entityDrawers: MutableMap<Long, Drawer> = HashMap()

    /**
     * Словарь, в котором хранится информация об экипировке объектов.
     * Это нужно, чтобы при смене состояния информация об экипировке не терялась.
     */
    private val equipment: MutableMap<Long, Equipment> = HashMap()

    /** Загружает шаблонные представления для всех состояний шаблона [templateName] и кэширует их */
    private fun loadBuildingTemplateDrawers(templateName: String) {
        cachedBuildingDrawerTemplates[templateName] = loadBuildingDrawersFromTemplate(sceneLoader, templateName)
    }

    private fun loadEntityTemplateDrawers(templateName: String) {
        cachedEntityDrawerTemplates[templateName] = loadEntityDrawersFromTemplate(sceneLoader, templateName)
    }

    override fun getEntityHitbox(entityID: Long): Rectangle? = entityDrawers[entityID]?.hitBox

    override fun drawBuilding(buildingID: Long, virtualScreen: VirtualScreen, x: Float, y: Float, isOpaque: Boolean, currentTimeMillis: Long) {
        val drawer : Drawer = buildingDrawers[buildingID] ?: run {
            Log.e("Drawer not loaded. buildingID=$buildingID")
            return
        }
        drawer.draw(virtualScreen, x, y, isOpaque, currentTimeMillis)
    }

    override fun drawEntity(entityID: Long, virtualScreen: VirtualScreen, x: Float, y: Float, isOpaque: Boolean, currentTimeMillis: Long, moveDirection: MoveDirection) {
        val drawer : Drawer = entityDrawers[entityID] ?: run {
            Log.e("Drawer not loaded. entityID=$entityID")
            return
        }
        drawer.draw(virtualScreen, x, y, isOpaque, currentTimeMillis, moveDirection)
    }

    override fun loadDrawers(initialState: SimplifiedState, currentTimeMillis: Long) {
        for ((id, building) in initialState.buildings) {
            loadBuildingDrawer(id, building, currentTimeMillis)
        }
        for ((id, entity) in initialState.entities) {
            loadEntityDrawer(id, entity, currentTimeMillis)
        }
    }

    override fun updateDrawers(stateDifference: StateDifference, oldState: SimplifiedState, currentTimeMillis: Long) {
        for ((id, obj) in stateDifference.buildingsDifference.new) {
            loadBuildingDrawer(id, obj, currentTimeMillis)
        }
        for ((id, newObj) in stateDifference.buildingsDifference.changed) {
            val oldObj = oldState.buildings[id]
            if (oldObj == null || newObj.template != oldObj.template) {
                loadBuildingDrawer(id, newObj, currentTimeMillis)
                continue
            }
            if (newObj.state != oldObj.state) {
                loadBuildingDrawer(id, newObj, currentTimeMillis)
                continue
            }
        }
        for ((id, obj) in stateDifference.entitiesDifference.new) {
            loadEntityDrawer(id, obj, currentTimeMillis)
        }
        for ((id, newObj) in stateDifference.entitiesDifference.changed) {
            val oldObj = oldState.entities[id]
            if (oldObj == null || newObj.template != oldObj.template) {
                loadEntityDrawer(id, newObj, currentTimeMillis)
                continue
            }
            if (newObj.state != oldObj.state) {
                loadEntityDrawer(id, newObj, currentTimeMillis)
                continue
            }
            if (newObj.action != oldObj.action) {
                setAction(id, newObj.action, currentTimeMillis)
            }
            if (newObj.isMoving != oldObj.isMoving) {
                setMoving(id, newObj.isMoving, currentTimeMillis)
            }
        }
    }

    override fun updateEquipment(entityID: Long, entity: SimplifiedEntity, equipment: Equipment, currentTimeMillis: Long) {
        this.equipment[entityID] = equipment
        loadEntityDrawer(entityID, entity, currentTimeMillis)
    }

    private fun loadBuildingDrawer(buildingID: Long, building: SimplifiedBuilding, currentTimeMillis: Long) {
        if (cachedBuildingDrawerTemplates[building.template] == null) {
            loadBuildingTemplateDrawers(building.template)
        }
        val templateDrawerStates : Map<String, DrawerTemplate>? = cachedBuildingDrawerTemplates[building.template]
        val drawer = if (templateDrawerStates == null) {
            Log.e("Error while loading drawer from a template. (buildingID=$building template=${building.template})")
            DrawerImpl(EmptyDrawerTemplate())
        }
        else {
            val template: DrawerTemplate = templateDrawerStates[building.state] ?:
            templateDrawerStates["default"] ?: run {
                Log.e("Error: template=${building.template} state=${building.state}: can't load neither state nor default state.")
                EmptyDrawerTemplate()
            }
            DrawerImpl(template, currentTimeMillis)
        }
        buildingDrawers[buildingID] = drawer
    }

    private fun loadEntityDrawer(entityID: Long, entity: SimplifiedEntity, currentTimeMillis: Long) {
        val drawer: Drawer
        if (cachedEntityDrawerTemplates[entity.template] == null) {
            loadEntityTemplateDrawers(entity.template)
        }
        val templateDrawerStates : Map<String, DrawerTemplate>? = cachedEntityDrawerTemplates[entity.template]
        if (templateDrawerStates == null) {
            Log.e("Error while loading drawer from a template. (entityID=$entityID template=${entity.template})")
            drawer = DrawerImpl(EmptyDrawerTemplate())
        }
        else {
            val template: DrawerTemplate = templateDrawerStates[entity.state] ?:
                templateDrawerStates["default"] ?: run {
                    Log.e("Error: template=${entity.template} state=${entity.state}: can't load neither state nor default state.")
                    EmptyDrawerTemplate()
                }

            val objEquipment = equipment[entityID]
            drawer = if (template is HumanoidDrawerTemplate && objEquipment != null && objEquipment != template.equipment) {
                DrawerImpl(HumanoidDrawerTemplate(objEquipment), currentTimeMillis)
            }
            else DrawerImpl(template, currentTimeMillis)
        }
        drawer.setAction(entity.action, currentTimeMillis)
        drawer.setMoving(entity.isMoving, currentTimeMillis)
        entityDrawers[entityID] = drawer
    }

    private fun setAction(entityID: Long, newAction: String, currentTimeMillis: Long) {
        entityDrawers[entityID]?.setAction(newAction, currentTimeMillis)
    }

    private fun setMoving(entityID: Long, newMoving: Boolean, currentTimeMillis: Long) {
        entityDrawers[entityID]?.setMoving(newMoving, currentTimeMillis)
    }

}