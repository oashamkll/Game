package com.mygame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont

class MainGame : ApplicationAdapter() {
    private lateinit var camera: PerspectiveCamera
    private lateinit var modelBatch: ModelBatch
    private lateinit var assets: MutableList<ModelInstance>
    private lateinit var environment: Environment
    private lateinit var camController: CameraInputController
    private lateinit var hudBatch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var builder: ModelBuilder

    override fun create() {
        modelBatch = ModelBatch()
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(15f, 15f, 15f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 1f
        camera.far = 500f
        camera.update()

        builder = ModelBuilder()
        assets = mutableListOf()

        // 1. Create a beautiful Floor
        val floorTex = Texture(Gdx.files.internal("textures/grass.png"))
        floorTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val floorMaterial = Material(TextureAttribute.createDiffuse(floorTex))
        val floor = builder.createRect(-50f, 0f, -50f, 50f, 0f, -50f, 50f, 0f, 50f, -50f, 0f, 50f, 0f, 1f, 0f,
            floorMaterial, (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong())
        assets.add(ModelInstance(floor))

        // 2. Create the Character (Humanoid Proxy)
        val charTex = Texture(Gdx.files.internal("textures/character_diffuse.png"))
        val charMaterial = Material(TextureAttribute.createDiffuse(charTex))
        val character = builder.createCapsule(1f, 4f, 16, charMaterial, 
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong())
        val charInstance = ModelInstance(character)
        charInstance.transform.setToTranslation(0f, 2f, 0f)
        assets.add(charInstance)

        // 3. Environment (Lighting & Sky)
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, -0.5f))

        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController
        
        hudBatch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(2f)
    }

    override fun render() {
        camController.update()
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f) // Sky Blue
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(assets, environment)
        modelBatch.end()
        
        hudBatch.begin()
        font.draw(hudBatch, "ECC REAL 3D WORLD v2.0", 30f, Gdx.graphics.height - 30f)
        font.draw(hudBatch, "FPS: " + Gdx.graphics.framesPerSecond, 30f, Gdx.graphics.height - 70f)
        font.draw(hudBatch, "USE MOUSE TO LOOK AROUND", 30f, 50f)
        hudBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
        assets.forEach { it.model.dispose() }
        hudBatch.dispose()
        font.dispose()
    }
}
