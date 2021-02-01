package eu.antoniolopez.xmlparser

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import eu.antoniolopez.xmlparser.domain.Line
import eu.antoniolopez.xmlparser.domain.Member
import eu.antoniolopez.xmlparser.domain.Node
import eu.antoniolopez.xmlparser.domain.Osm
import eu.antoniolopez.xmlparser.domain.PathText
import eu.antoniolopez.xmlparser.domain.Relation
import eu.antoniolopez.xmlparser.domain.Rule
import eu.antoniolopez.xmlparser.domain.Rules
import eu.antoniolopez.xmlparser.domain.Tag
import eu.antoniolopez.xmlparser.domain.Way
import java.io.File

val xmlMapper = XmlMapper(
    JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }
).apply {
    configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
    enable(SerializationFeature.INDENT_OUTPUT)
    enable(SerializationFeature.WRAP_ROOT_VALUE)
    registerKotlinModule()
}

fun main(args: Array<String>) {
    val originFile = "src/main/resources/catalunya_trails_trailforks.osm"
    val destinationFile = "src/main/resources/catalunya_trails_trailforks2.osm"
    val setOfNodes = mutableSetOf<Integer>()
    val osm = convertXmlFile2DataObject(originFile)

    val ways = filterWays(osm)

    // filter valid nodes
    val validNodes = run {
        ways.map { it.nd }.forEach { it.forEach { node -> setOfNodes.add(node.ref) } }
        osm.node.filter { setOfNodes.contains(it.id) }
    }

    // transform ways
    ways.forEach { way ->
        //it.tag.removeAll { !it.k.matches("name|tf:difficulty".toRegex()) }
        /*
        val color = difficultyToColor(way)
        way.tag.add(Tag(k = "color", v = color))
        way.tag.add(Tag(k = "colour:text", v = color))
        way.tag.add(Tag(k = "width", v = "2"))
         */
        addTrailNameToNode(way, validNodes)
    }

    val newOsm = osm.copy(node = validNodes, way = ways)
    write2XMLFile(newOsm, destinationFile)
}

private fun convertXmlFile2DataObject(originFile: String): Osm {
    val file = File(originFile)
    return xmlMapper.readValue(file, Osm::class.java)
}

private fun write2XMLFile(obj: Any, pathFile: String) {
    xmlMapper.writeValue(File(pathFile), obj)
}

private fun addTrailNameToNode(
    way: Way,
    validNodes: List<Node>
) {
    val name = way.tag.first { it.k == "name" }.v
    val nodeId = way.nd[way.nd.size / 2].ref
    val node = validNodes.first { node -> node.id == nodeId }
    if (node.tag == null) {
        node.tag = mutableListOf()
    }
    node.tag!!.apply {
        add(Tag(k = "name", v = name))
        add(Tag(k = "place", v = "locality"))
    }
}

// filter valid ways  <tag k="tf:difficulty" v="3"/>   3,4,5,8,9,10
private fun filterWays(osm: Osm) = osm.way.filter { way ->
    way.tag.firstOrNull { tag -> tag.k == "tf:difficulty" && tag.v.matches("3|4|5|8|9|10".toRegex()) } != null
        && way.tag.firstOrNull { tag -> tag.k == "tf:region" && tag.v == "Gavarres" } != null
}

private fun difficultyToColor(way: Way) =
    when (way.tag.first { tag -> tag.k == "tf:difficulty" }.v) {
        "3" -> "green"
        "4" -> "blue"
        "9" -> "red"
        else -> "black"
    }

private fun getRelations(ways: List<Way>): List<Relation> {
    val tags = mutableListOf<Tag>()
    tags.add(Tag(k = "colour", v = "red"))
    tags.add(Tag(k = "sport", v = "multi"))
    tags.add(Tag(k = "surface", v = "tartan"))
    tags.add(Tag(k = "type", v = "multipolygon"))

    val members = ways.map {
        Member(
            type = "way",
            ref = it.id.toString(),
            role = "inner"
        )
    }
    return listOf(
        Relation(
            id = "2376726",
            tag = tags,
            member = members
        )
    )
}

// This only applies to Mapnik
private fun getRules(): Rules {
    val green = "#1AB826"
    val blue = "#1329E1"
    val red = "#E13813"
    val black = "#000000"
    val greenLine = Line(stroke = green)
    val greenPathText = PathText(stroke = green)
    val greenRule = Rule(e = "way", k = "tf:difficulty", v = "3", line = greenLine, pathText = greenPathText)
    val blueRule =
        greenRule.copy(v = "4", line = greenLine.copy(stroke = blue), pathText = greenPathText.copy(stroke = blue))
    val redRule =
        greenRule.copy(v = "9", line = greenLine.copy(stroke = blue), pathText = greenPathText.copy(stroke = blue))
    val blackRule =
        greenRule.copy(v = "10", line = greenLine.copy(stroke = red), pathText = greenPathText.copy(stroke = red))
    val proLineRule =
        greenRule.copy(v = "8", line = greenLine.copy(stroke = black), pathText = greenPathText.copy(stroke = black))
    val blackDiamondRule =
        greenRule.copy(v = "5", line = greenLine.copy(stroke = black), pathText = greenPathText.copy(stroke = black))
    return Rules(listOf(greenRule, blueRule, redRule, blackRule, proLineRule, blackDiamondRule))
}
