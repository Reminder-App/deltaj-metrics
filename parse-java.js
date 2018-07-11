var fs = require('fs')
var lr = require('readline')
var stringify = require('csv-stringify')

var Class    = ''
var Func     = ''
var FuncType = ''

var Env      = []


function main () {

  var dirs = fs.readdirSync('./javas')

  // for (dir of dirs) {
    var lineReader = lr.createInterface({
      input: fs.createReadStream('./javas/AddReminderActivity.java')
    })
  
    lineReader.on('line', (l) => {
      handleLine(l.trim())      
    })

    // lineReader.on('close', () => {
    //   // console.log(Env)
    //   stringify(Env, function(err, output){
    //     console.log(output)
    //   });
    //   // writeCSV()
    // })
  // }

}


function saveOrUpdateEnv (feature) {
  let exists = false
  
  for (obj of Env) {
    if (obj.feature == feature && obj.class == Class) {
      obj.active = true
      exists     = true
    }
  }

  if (!exists) {
    Env.push({
      class: Class,
      feature: feature,
      // function: {
      //   type: FuncType,
      //   name: Func
      // },
      counter: 0,
      active: true
    })
  }
}

function handleLine (line) {

  if (line.startsWith('public class ')) {
    Class = line.substring(6).split(' extends ')[0]
    console.log(Class)
  }

  // else if (line.startsWith('protected ')) {
  //   let data = line.split(/[\s,]+/)
  //   FuncType = data[1]
  //   Func     = data[2].split('(')[0] + '()'
  //   console.log(FuncType)
  // }

  // else if (line.startsWith('private ')) {
  //   let data = line.split(/[\s,]+/)
  //   FuncType = data[1]
  //   Func     = data[2].split('(')[0] + '()'
  //   console.log(FuncType)
  // }

  else if (line.startsWith('//#if ')) {
    const fs = line.substring(6).split(' && ')
    for (f of fs) {
      saveOrUpdateEnv(f)  
    }
  }

  else if (line.startsWith('//#ifdef ')) {
    const f = line.substring(9)
    saveOrUpdateEnv(f)    
  }

  else if (line.startsWith('//#endif')) {
    for (f of Env) {
      if (f.active) {
        f.active = false
      }
    }
  }

  // Count active feature lines
  for (f of Env) {
    if (f.active) {
      f.counter = f.counter + 1
    }
  }
}


main ()