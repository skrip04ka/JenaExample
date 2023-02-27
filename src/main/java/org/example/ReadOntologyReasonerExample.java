package org.example;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.transitiveReasoner.TransitiveReasoner;
import org.apache.jena.reasoner.transitiveReasoner.TransitiveReasonerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.StringJoiner;

import static org.apache.jena.ontology.OntModelSpec.OWL_MEM;

public class ReadOntologyReasonerExample {

    private static final String nameSpace = "http://jena-test#";

    public static void main(String[] args) {
        Model model = ModelFactory.createDefaultModel();
        OntModel ontModel = ModelFactory.createOntologyModel(OWL_MEM, model);

        try {
            InputStream inputStream = new FileInputStream("src/main/resources/wine_and_cheese_v1.owl");
            ontModel.read(inputStream, "RDF/XML-ABBREV");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        ObjectProperty goesWith = ontModel.getObjectProperty(nameSpace + "goes_with");

        // Reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner.bindSchema(model.getGraph());

        InfModel inf = ModelFactory.createInfModel(reasoner, model);


        Iterator<Resource> iter = inf.listResourcesWithProperty(goesWith);

        while (iter.hasNext()) {
            Statement s = iter.next().getProperty(goesWith);

            System.out.println(new StringJoiner(" ")
                    .add(s.getSubject().getLocalName())
                    .add(s.getPredicate().getLocalName())
                    .add(s.getObject().asResource().getLocalName()));

        }


    }

}
