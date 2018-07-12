var fs = require('fs')
var lr = require('readline')
var stringify = require('csv-stringify')

const folder = './javas'

// context state
var Env      = []
var Class    = ''

// track nested features
var Track    = []


async function main () {

  var dirs = fs.readdirSync(folder)

  for (dir of dirs) {
    Class = dir.split('.')[0]

    await loop (dir)
  }

  stringify(Env, function(err, output){
    console.log(output)
  });
}

function loop (dir) {
  return new Promise( (resolve, reject) => {
    var lineReader = lr.createInterface({
      input: fs.createReadStream(folder + dir)
    })
  
    lineReader.on('line', (l) => {
      handleLine(l.trim())
    })
  
    lineReader.on('close', () => {
      resolve()
    })
  })
}


function saveOrUpdateEnv (feature) {
  let exists = false

  for (obj of Env) {
    if (obj.feature == feature && obj.jClass == Class) {
      obj.active = true
      exists     = true
    }
  }

  if (!exists) {
    Env.push({
      jClass: Class,
      feature: feature,
      counter: 0,
      active: true
    })
  }
}

function handleLine (line) {


  if (line.startsWith('//#if ')) {
    // parsing feature expression cases
    let fs
    let unique = false

    if (line.includes('&&') && line.includes('||')) {
      fs = line.substring(6)
      fs = fs.split(/[^a-zA-Z]/)
             .filter( (v) => v !== '')
             .sort()
             .reduce((init, current) => {
               if (init.length === 0 || init[init.length - 1] !== current) {
                 init.push(current);
               }
               return init;
             }, [])
      Track.push(fs)
    }

    else if (line.includes('&&')) {
      fs = line.substring(6).split(' && ')
      Track.push(fs)
    }

    else if (line.includes('||')) {
      fs = line.substring(6).split(' || ')
      Track.push(fs)
    }

    else {
      fs = line.substring(6)
      Track.push(fs)
      unique = true
    }

    if (unique) {
      saveOrUpdateEnv(fs)
    } else {
      for (f of fs) {
        saveOrUpdateEnv(f)
      }
    }

  }

  else if (line.startsWith('//#ifdef ')) {
    const f = line.substring(9)

    // check if expression is nested
    if (Track.length > 0 && Track[0] instanceof Array) {
      const ds = Track[0].filter( (v) => v !== f)[0]
      setInactive(ds)
    }
    Track.push(f)
    saveOrUpdateEnv(f)
  }

  else if (line.startsWith('//#elifdef ')) {
    const f = line.substring(11)

    if (Track.length > 0 && Track[0] instanceof Array) {
      const ds = Track[0].filter( (v) => v !== f)[0]
      setInactive(ds)
    }

    saveOrUpdateEnv(f)
    setInactive(Track.pop())
    Track.push(f)
  }

  else if (line.startsWith('//#endif')) {
    if (Track.length > 1 && Track[0] instanceof Array) {
      for (f of Track[0]) {
        setActive(f)
      }
      Track.pop()
    } else {
      setInactive(Track.pop())
    }
  }

  else {
    // Count active feature lines
    for (f of Env) {
      if (f.active) {
        f.counter = f.counter + 1
      }
    }
  }

}

function setInactive (feature) {
  if (feature instanceof Array) {
    for (ff of feature) {
      for (f of Env) {
        if (f.feature == ff && f.jClass == Class) {
          f.active = false
        }
      }
    }
  } else {
    for (f of Env) {
      if (f.feature == feature && f.jClass == Class) {
        f.active = false
      }
    }
  }
}

function setActive (feature) {
  for (f of Env) {
    if (f.feature == feature && f.jClass == Class) {
      f.active = true
    }
  }
}


main ()
