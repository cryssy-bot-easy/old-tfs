<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron"
            xmlns:iso="http://purl.oclc.org/dsdl/schematron">

    <sch:ns uri="http://www.topologi.com/add" prefix="add"/>

    <sch:pattern name="Check structure">
        <sch:rule context="/">
            <sch:assert test="add:add">The root element must be add.</sch:assert>
        </sch:rule>
        <sch:rule context="add:add">
            <sch:assert test="@sum">The element add must have a sum attribute</sch:assert>
            <sch:assert test="count(*) = count(add:item)">The element add can only have item child elements.</sch:assert>
            <sch:assert test="count(add:item) >= 1">The element add must have at least one item element.</sch:assert>
        </sch:rule>
        <sch:rule context="add:item">
            <sch:assert test="number(.)">The content of the item element must be a number.</sch:assert>
        </sch:rule>
    </sch:pattern>
    <sch:pattern name="Check math">
        <sch:rule context="add:add">
            <sch:assert test="@sum = sum(add:item)">The value of the sum attribute should be the
                sum of all the values in the item child elements.</sch:assert>
        </sch:rule>
    </sch:pattern>
</sch:schema>
