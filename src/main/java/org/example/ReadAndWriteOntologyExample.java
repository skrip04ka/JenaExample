package org.example;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

import java.io.*;
import java.util.Iterator;
import java.util.StringJoiner;

import static org.apache.jena.ontology.OntModelSpec.OWL_MEM;

public class ReadAndWriteOntologyExample {

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


        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner.bindSchema(model.getGraph());

        InfModel inf = ModelFactory.createInfModel(reasoner, model);


        ObjectProperty goesWith = ontModel.getObjectProperty(nameSpace + "goes_with");

        Iterator<Resource> iter = inf.listResourcesWithProperty(goesWith, false);
        while (iter.hasNext()) {
            ontModel.add(iter.next().getProperty(goesWith));
        }


        try {
            OutputStream outputStream = new FileOutputStream("src/main/resources/wine_and_cheese_v2.owl");
            ontModel.write(outputStream, "RDF/XML-ABBREV");
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
