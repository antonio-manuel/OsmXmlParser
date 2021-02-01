package eu.antoniolopez.xmlparser.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

data class Bounds(
    @JacksonXmlProperty(isAttribute = true)
    val minlat: Float,
    @JacksonXmlProperty(isAttribute = true)
    val minlon: Float,
    @JacksonXmlProperty(isAttribute = true)
    val maxlat: Float,
    @JacksonXmlProperty(isAttribute = true)
    val maxlon: Float,
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Node(
    @JacksonXmlProperty(isAttribute = true)
    val id: Integer,
    @JacksonXmlProperty(isAttribute = true)
    val version: Integer,
    @JacksonXmlProperty(isAttribute = true)
    val lat: Float,
    @JacksonXmlProperty(isAttribute = true)
    val lon: Float,
    @JacksonXmlElementWrapper(useWrapping = false)
    var tag: MutableList<Tag>? = null  // TODO Improve to not write this if empty
)

data class Way(
    @JacksonXmlProperty(isAttribute = true)
    val id: Integer,
    @JacksonXmlProperty(isAttribute = true)
    val version: Integer,
    @JacksonXmlElementWrapper(useWrapping = false)
    val nd: List<Nd>,
    @JacksonXmlElementWrapper(useWrapping = false)
    val tag: MutableList<Tag>
)

data class Nd(
    @JacksonXmlProperty(isAttribute = true)
    val ref: Integer
)

data class Tag(
    @JacksonXmlProperty(isAttribute = true)
    val k: String,
    @JacksonXmlProperty(isAttribute = true)
    val v: String
)

@JacksonXmlRootElement(localName = "osm")
data class Osm(
    @JacksonXmlProperty(isAttribute = true)
    val version: String,
    @JacksonXmlProperty(isAttribute = true)
    val generator: String,
    val bounds: Bounds,
    @JacksonXmlElementWrapper(useWrapping = false)
    val node: List<Node>,
    @JacksonXmlElementWrapper(useWrapping = false)
    val way: List<Way>
)

data class Member(
    @JacksonXmlProperty(isAttribute = true)
    val type: String,
    @JacksonXmlProperty(isAttribute = true)
    val ref: String,
    @JacksonXmlProperty(isAttribute = true)
    val role: String = ""
)

data class Relation(
    @JacksonXmlProperty(isAttribute = true)
    val id: String,
    @JacksonXmlProperty(isAttribute = true)
    val version: String = "1",
    @JacksonXmlElementWrapper(useWrapping = false)
    val tag: List<Tag>,
    @JacksonXmlElementWrapper(useWrapping = false)
    val member: List<Member>
)


// This is OSMARENDER
data class Line(
    @JacksonXmlProperty(isAttribute = true, localName = "stroke")
    val stroke: String,
    @JacksonXmlProperty(isAttribute = true, localName = "stroke-width")
    val strokeWidth: String = "1"
)

data class PathText(
    @JacksonXmlProperty(isAttribute = true)
    val k: String = "name",
    @JacksonXmlProperty(isAttribute = true, localName = "text-anchor")
    val textAnchor: String = "middle",
    @JacksonXmlProperty(isAttribute = true, localName = "stroke")
    val stroke: String,
    @JacksonXmlProperty(isAttribute = true, localName = "stroke-width")
    val strokeWidth: String = "1"
)

data class Rule(
    @JacksonXmlProperty(isAttribute = true)
    val e: String,
    @JacksonXmlProperty(isAttribute = true)
    val k: String,
    @JacksonXmlProperty(isAttribute = true)
    val v: String,
    val line: Line,
    val pathText: PathText
)

data class Rules(
    @JacksonXmlElementWrapper(useWrapping = false)
    val rule: List<Rule>
)
