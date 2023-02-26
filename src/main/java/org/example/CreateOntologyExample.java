package org.example;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.XSD;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.StringJoiner;

import static org.apache.jena.ontology.OntModelSpec.OWL_MEM;

public class CreateOntologyExample {
    private static final String nameSpace = "http://jena-test#";

    public static void main(String[] args) {
        Model model = ModelFactory.createDefaultModel();
        OntModel ontModel = ModelFactory.createOntologyModel(OWL_MEM, model);

        // Wine Class
        OntClass wine = ontModel.createClass(nameSpace + "Wine");

        OntClass redWine = ontModel.createClass(nameSpace + "RedWine");
        OntClass whiteWine = ontModel.createClass(nameSpace + "WhiteWine");
        whiteWine.addDisjointWith(redWine);

        wine.addSubClass(redWine);
        wine.addSubClass(whiteWine);


        // Cheese Class
        OntClass cheese = ontModel.createClass(nameSpace + "Cheese");
        cheese.addDisjointWith(wine);

        OntClass oldCheese = ontModel.createClass(nameSpace + "OldCheese");
        OntClass youngCheese = ontModel.createClass(nameSpace + "YoungCheese");
        youngCheese.addDisjointWith(oldCheese);

        cheese.addSubClass(oldCheese);
        cheese.addSubClass(youngCheese);


        // Object prop
        ObjectProperty goesWith = ontModel.createObjectProperty(nameSpace + "goes_with");
        goesWith.convertToSymmetricProperty();


        // Data prop
        DatatypeProperty hasColor = ontModel.createDatatypeProperty(nameSpace + "has_color");
        hasColor.convertToFunctionalProperty();
        hasColor.addDomain(wine);
        hasColor.addRange(XSD.xstring);


        // Individual wine
        Individual abrauDurso = wine.createIndividual(nameSpace + "AbrauDurso");
        abrauDurso.addProperty(hasColor, "red");

        Individual usWine1974 = wine.createIndividual(nameSpace + "UsWine1974");
        usWine1974.addProperty(hasColor, "white");

        Individual beaulieuVineyard = wine.createIndividual(nameSpace + "BeaulieuVineyard");
        beaulieuVineyard.addProperty(hasColor, "white");

        // Individual cheese
        Individual dorBlue = youngCheese.createIndividual(nameSpace + "DorBlue");
        Individual parmesan = oldCheese.createIndividual(nameSpace + "parmesan");
        Individual premiumParmesan = oldCheese.createIndividual(nameSpace + "premiumParmesan");



        // logic
        redWine.addEquivalentClass(ontModel.createIntersectionClass(null,
                ontModel.createList(wine,
                        ontModel.createHasValueRestriction(null, hasColor, abrauDurso.getPropertyValue(hasColor)))));

        whiteWine.addEquivalentClass(ontModel.createIntersectionClass(null,
                ontModel.createList(wine,
                        ontModel.createHasValueRestriction(null, hasColor, usWine1974.getPropertyValue(hasColor)))));


        // Data
        dorBlue.addProperty(goesWith, usWine1974);
        parmesan.addSameAs(premiumParmesan);
        usWine1974.addSameAs(beaulieuVineyard);
        premiumParmesan.addProperty(goesWith, abrauDurso);

        // Reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner.bindSchema(model.getGraph());

        InfModel inf = ModelFactory.createInfModel(reasoner, model);
//        Resource a = inf.getResource(nameSpace + "parmesan");
//        Statement s = a.getProperty(goesWith);
//        System.out.println("Statement: " + s);

        Iterator<Resource> iter = inf.listResourcesWithProperty(goesWith);
        while (iter.hasNext()) {
            Statement s = iter.next().getProperty(goesWith);

            System.out.println(new StringJoiner(" ")
                    .add(s.getSubject().getLocalName())
                    .add(s.getPredicate().getLocalName())
                    .add(s.getObject().asResource().getLocalName()));

//            System.out.println(s);
        }

        // Save as .owl file
        try {
            OutputStream outputStream = new FileOutputStream("src/main/resources/wine_and_cheese_v1.owl");
            ontModel.write(outputStream, "RDF/XML-ABBREV");
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


