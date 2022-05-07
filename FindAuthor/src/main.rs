mod document;
mod grouping;
mod scanner;
mod token;

use document::*;
use scanner::Scanner;
use std::fs;
use std::io::Error;
use std::io::ErrorKind::Other;

fn main() -> Result<(), Error> {
    if std::env::args().len() < 2 {
        eprintln!("ERROR! Usage: FindAuthor [files]");
        eprintln!("Must have 1 or more arguments");
        return Err(Error::new(Other, "Invalid arguments"));
    }

    let weights = [11.0, 33.0, 50.0, 0.4, 4.0];

    let database = fs::read_dir("./SignatureFiles/")?
        .filter(|x| match x {
            Err(e) => {
                eprintln!("Failed to read database: {}", e);
                false
            }
            Ok(_) => true,
        })
        .map(|x| x.unwrap())
        .filter(|x| {
            let res = x.metadata();
            if let Ok(met) = res {
                if !met.is_file() {
                    eprintln!("WARN: Non-file in database folder");
                    false
                } else {
                    println!("Reading {}", x.path().to_str().unwrap_or("unknown"));
                    true
                }
            } else {
                eprintln!("Metadata not available: {}", res.unwrap_err());
                false
            }
        })
        .map(|x| LinguisticFeatures::from_file(x.path()))
        .filter(|x| match x {
            Err(e) => {
                eprintln!("Failed to load database element: {}", e);
                false
            }
            v => {
                let v = v.as_ref().unwrap();
                println!("Loaded: {:#?}\n", v);
                true
            }
        })
        .map(|x| x.unwrap())
        .collect::<Vec<LinguisticFeatures>>();

    for result in std::env::args()
        .skip(1)
        .map(|x| -> Result<LinguisticFeatures, Error> {
            let doc = Document::new(Scanner::new(Box::new(fs::File::open(&x)?)));

            println!("\n\n{:-^1$}", x, 100);

            let feats = doc.get_features();
            // println!("Trying to identify: {:#?} = {}\n", feats, doc);
            println!("Trying to identify: {:#?}\n", feats);
            Ok(feats)
        })
        .filter(|x| match x {
            Err(e) => {
                eprintln!("Failed to load file: {}", e);
                false
            }
            Ok(_) => true,
        })
        .map(|id| -> Result<(), Error> {
            let id = id.unwrap();

            let best = database
                .iter()
                .map(|ret| {
                    let err = ret.rmse(&id, &weights);

                    println!("Candidate {: <30} delta={}", ret.get_name(), err);

                    (ret, err)
                })
                .reduce(|a, b| if a.1 < b.1 { a } else { b })
                .ok_or_else(|| Error::new(Other, "reduce() failed??"))?;

            println!("\nBEST MATCH: {:#?} (delta={})", best.0, best.1,);
            Ok(())
        })
    {
        result?;
    }

    Ok(())
}
